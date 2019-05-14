package com.gleme.unifei;

import java.io.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.util.BitSet;
import com.gleme.unifei.antlr4.AULLexer;
import com.gleme.unifei.antlr4.AULParser;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class AUL {

    private static boolean errorFlag = false;

    public static void main(String[] args) {

        boolean printTokenFile = false;
        String className = "App";
        File sourceFile = null;
        try {
            switch (args.length) {
                case 0:
                    printUsageMessage();
                    System.exit(0);
                    break;

                case 1:
                    sourceFile = new File(args[0]);
                    break;

                case 2:
                    if (args[0] == "-t") {
                        printTokenFile = true;
                    } else {
                        className = args[0].substring(0, 1).toUpperCase() + args[0].substring(1).toLowerCase();
                    }
                    sourceFile = new File(args[1]);
                    break;

                case 3:
                    printTokenFile = true;
                    className = args[1].substring(0, 1).toUpperCase() + args[1].substring(1).toLowerCase();
                    sourceFile = new File(args[2]);
                    break;

                default:
                    System.out.println("AUL Error: invalid number of arguments!");
                    printUsageMessage();
                    System.exit(1);
            }

            if(!sourceFile.exists()) {
                System.out.println("AUL Error: source file not found.");
                System.exit(1);
            }

            ANTLRInputStream inputFile = new ANTLRInputStream(new FileInputStream(sourceFile));
            AULLexer lexer = new AULLexer(inputFile);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            AULParser parser = new AULParser(tokens);

            parser.addErrorListener(new ANTLRErrorListener() {

                @Override
                public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int arg2, int arg3, String arg4, RecognitionException arg5) {
                    System.out.println("iERROR: (" + arg2 + ", " + arg3 + ") SyntaxError -- " + arg5.getMessage());
                }

                @Override
                public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {
                }

                @Override
                public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {
                }

                @Override
                public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5, ATNConfigSet arg6) {
                }
            });

            ParseTree tree = parser.program();

            ParseTreeWalker walker = new ParseTreeWalker();
            JavaTranslator translator = new JavaTranslator(className);
            walker.walk(translator, tree);

            PrintWriter writer = new PrintWriter(className + ".java", "UTF-8");
            writer.print(translator.codeBuffer.toString());
            writer.close();

            if (printTokenFile) {
                writer = new PrintWriter(className + ".tokens", "UTF-8");
                writer.print(translator.tokensBuffer.toString());
                writer.close();
            }

            Process pro = Runtime.getRuntime().exec("javac " + className + ".java");
            errorMessage(pro.getErrorStream());
            pro.waitFor();

            pro = Runtime.getRuntime().exec("jar -cfe " + className + ".jar " + className + " " + className + ".class");
            errorMessage(pro.getErrorStream());
            pro.waitFor();

            if (!errorFlag) {
                System.out.println("Compilation successful, now you can run you program with:");
                System.out.println("\tjava -jar " + className + ".jar");
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

    private static void printUsageMessage() {
        System.out.println("usage: $ aulc [-t] [className] <sourceFile>");
        System.out.println("\t[className] parameter modifies the generated class name (App - default)");
        System.out.println("\t[-t] flag generates the token output file.");
    }

    private static void errorMessage(InputStream is) throws Exception {
        String line = null;

        BufferedReader buffReader = new BufferedReader(new InputStreamReader(is));

        while ((line = buffReader.readLine()) != null) {

            System.out.println("Error: " + line);
            errorFlag = true;
        }
    }

}
