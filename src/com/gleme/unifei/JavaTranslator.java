package com.gleme.unifei;

import com.gleme.unifei.antlr4.AULBaseListener;
import com.gleme.unifei.antlr4.AULParser;
import com.gleme.unifei.antlr4.AULParser.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public class JavaTranslator extends AULBaseListener {

    private String className;
    private int ruleLevel = 0;
    public StringBuilder codeBuffer = new StringBuilder();
    public StringBuilder rulesBuffer = new StringBuilder();
    public StringBuilder tokensBuffer = new StringBuilder();

    public JavaTranslator(String className) {
        this.className = className;
    }

    @Override
    public void enterBreakline(AULParser.BreaklineContext ctx) {
        codeBuffer.append("; ");
    }

    @Override
    public void enterElse_c(AULParser.Else_cContext ctx) {
        codeBuffer.append("else ");
    }

    @Override
    public void enterElseif_c(AULParser.Elseif_cContext ctx) {
        codeBuffer.append("else if (" + ctx.cond().getText() + ") ");
    }

    @Override
    public void enterIf_c(AULParser.If_cContext ctx) {
        codeBuffer.append("if (" + ctx.cond().getText() + ") ");
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

        rulesBuffer.append("\n");

        for (int i = 0; i < ruleLevel; i++) {
            rulesBuffer.append("\t");
        }

        rulesBuffer.append("[" + AULParser.ruleNames[ctx.getRuleIndex()] + "]");
        ruleLevel++;
    }

    @Override public void enterMethod_char(AULParser.Method_charContext ctx) {
        codeBuffer.append("char " + ctx.ID.getText() + "(");
    }

    @Override
    public void enterMethod_double(AULParser.Method_doubleContext ctx) {
        codeBuffer.append("double " + ctx.ID.getText() + "(");
    }

    @Override
    public void enterMethod_int(AULParser.Method_intContext ctx) {
        codeBuffer.append("int " + ctx.ID.getText() + "(");
    }

    @Override
    public void enterMethod_string(AULParser.Method_stringContext ctx) {
        codeBuffer.append("String " + ctx.ID.getText() + "(");
    }

    @Override
    public void enterMethod_void(AULParser.Method_voidContext ctx) {
        codeBuffer.append("void " + ctx.ID.getText() + "(");
    }

    @Override
    public void enterProgram(AULParser.ProgramContext ctx) {
        codeBuffer.append("import java.io.*; ");
        codeBuffer.append("public class " + className + " { ");
        codeBuffer.append("private static void cout(String format, Object... args) { try { System.out.print(String.format(format, args)); } catch (Exception e) { System.out.println(\"Exception: \" + e.getMessage()); } } ");
        codeBuffer.append("private static String cin() { String tmp = \"\"; try { BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); tmp = br.readLine(); } catch (Exception e) { System.out.println(\"Exception: \" + e.getMessage()); } return tmp; } ");
    }

    @Override
    public void enterRoot_main(AULParser.Root_mainContext ctx) {
        codeBuffer.append("public static void main(String[] args) ");
    }

    @Override
    public void enterRoot_method(AULParser.Root_methodContext ctx) {
        codeBuffer.append("private static ");
    }

    @Override
    public void enterStmt_assign(AULParser.Stmt_assignContext ctx) {
        codeBuffer.append(ctx.ID().getText() + " = " + ctx.expr().getText() + " ");
    }

    @Override
    public void enterCmd_for(AULParser.Cmd_forContext ctx) {

        codeBuffer.append("for (" + ctx.for_assign(0).getText() + "; " + ctx.cond().getText() + "; " + ctx.for_assign(1).getText() + ") ");
    }

    @Override
    public void enterStmt_return(AULParser.Stmt_returnContext ctx) {
        codeBuffer.append("return " + ctx.expr().getText());
    }

    @Override
    public void enterJava_cmd(AULParser.Java_cmdContext ctx) {
        codeBuffer.append(ctx.STRING_LIT().getText().substring(1, ctx.STRING_LIT().getText().length() - 1));
    }

    @Override
    public void enterStmt_method(AULParser.Stmt_methodContext ctx) {
        codeBuffer.append(ctx.ID().getText() + "(");
    }

    @Override
    public void enterCmd_while(AULParser.Cmd_whileContext ctx) {
        codeBuffer.append("while (" + ctx.cond().getText() + ") ");
    }

    @Override
    public void enterBlock(AULParser.BlockContext ctx) {
        codeBuffer.append("try { ");
    }

    @Override
    public void enterRet_block(AULParser.Ret_blockContext ctx) {
        codeBuffer.append("try { ");
    }

    @Override
    public void enterChar_var(AULParser.Char_varContext ctx) {

        codeBuffer.append("char " + ctx.ID().getText());

        if (ctx.getToken(AULParser.EQUALS, 0) != null) {
            codeBuffer.append(" = " + ctx.char_expr().getText());
        }
    }

    @Override
    public void enterDouble_var(AULParser.Double_varContext ctx) {

        codeBuffer.append("double " + ctx.ID().getText());

        if (ctx.getToken(AULParser.EQUALS, 0) != null) {
            codeBuffer.append(" = " + ctx.double_expr().getText());
        }
    }

    @Override
    public void enterInt_var(AULParser.Int_varContext ctx) {

        codeBuffer.append("int " + ctx.ID().getText());

        if (ctx.getToken(AULParser.EQUALS, 0) != null) {
            codeBuffer.append(" = " + ctx.int_expr().getText());
        }
    }

    @Override
    public void enterString_var(AULParser.String_varContext ctx) {

        codeBuffer.append("String " + ctx.ID().getText());

        if (ctx.getToken(AULParser.EQUALS, 0) != null) {
            codeBuffer.append(" = " + ctx.str_expr().getText());
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {

        ruleLevel--;
        rulesBuffer.append("\n");

        for (int i = 0; i < ruleLevel; i++) {
            rulesBuffer.append("\t");
        }

        rulesBuffer.append("[/" + AULParser.ruleNames[ctx.getRuleIndex()] + "]");
    }

    @Override
    public void exitMethod_args(AULParser.Method_argsContext ctx) {

        int i = 0;

        for (TerminalNode tn : ctx.getTokens(AULParser.ID)) {
            if (i != 0) {
                codeBuffer.append(", ");
            }


            if (ctx.var_type(i).getText().equals("string")) {
                codeBuffer.append("String " + tn.getText());
                i++;
            } else {
                codeBuffer.append(ctx.var_type(i++).getText() + " " + tn.getText());
            }

        }

        codeBuffer.append(") ");
    }

    @Override
    public void exitProgram(AULParser.ProgramContext ctx) {
        codeBuffer.append("}");
    }

    @Override
    public void exitStmt_method(AULParser.Stmt_methodContext ctx) {
        codeBuffer.append("); ");
    }

    @Override
    public void exitBlock(AULParser.BlockContext ctx) {
        codeBuffer.append("} catch (Exception e) { System.out.println(\"Syntax Error: \" + e.getMessage()); } ");
    }

    @Override
    public void exitRet_block(AULParser.Ret_blockContext ctx) {
        codeBuffer.append("} catch (Exception e) { System.out.println(\"Syntax Error: \" + e.getMessage()); } return " + ctx.ret + "; ");
    }

    @Override
    public void exitVoid_args(AULParser.Void_argsContext ctx) {

        for (int i = 0; i < ctx.expr().size(); i++) {

            if (i != 0) {
                codeBuffer.append(", ");
            }
            codeBuffer.append(ctx.expr(i).getText());
        }
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        tokensBuffer.append(AULParser.tokenNames[node.getSymbol().getType()] + "\t\t" + node.getText().replaceAll("\n", "\\\\n") + "\n");
        rulesBuffer.append(node.getText() + " ");

        if (node.getSymbol().getType() == AULParser.L_BRACE) {
            codeBuffer.append("{ ");

        } else if (node.getSymbol().getType() == AULParser.R_BRACE) {
            codeBuffer.append("} ");
        }
    }
}
