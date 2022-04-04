package ia.samuelvanie.refutation.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.python.util.PythonInterpreter;
import org.python.core.PyList;
import org.python.core.PyTuple;
import org.python.core.PyObject;
import org.python.core.PyString;

public class Refutation {
  static final List<String> POSSIBLE_OPERATORS = Arrays.asList(new String[] { "!", "&", "|", ">", "=", "(", ")" });

  static public boolean isOperator(String op) {
    return POSSIBLE_OPERATORS.contains(op);
  }

  static public List<String> tokenList(String s){
  	List<String> liste = new ArrayList<String>();
  	String arrayS[] = s.split("");
  	liste = Arrays.asList(arrayS);
  	return liste;
  }

  static public List<String> segmentSentence(String s) {
    List<String> segmentedSentences = new ArrayList<String>();
    int i = 0;
    int L = s.length();
    String literal;

    while (i < L) {
      if (isOperator(Character.toString(s.charAt(i)))) {
        segmentedSentences.add(Character.toString(s.charAt(i)));
        i++;
      } else if (Character.toString(s.charAt(i)).equals(" ")) {
        i++;
      } else {
        literal = "";
        while (i < L && !isOperator(Character.toString(s.charAt(i))) && !(Character.toString(s.charAt(i)).equals(" "))) {
          literal+=Character.toString(s.charAt(i));
          i += 1;
        }
        segmentedSentences.add(literal);
      }
    }

    return segmentedSentences;
  }

  static public Map<List<String>, Integer> forwardSlice(List<String> s, Integer index) {
    int balance = 0;
    int i = index;

    while (i < s.size()) {
      balance = s.get(i).equals("(") ? balance + 1
          : s.get(i).equals(")") ? balance-1 : balance;
      if (balance == 0 && !(s.get(i).equals("!"))) {
        Map<List<String>, Integer> result = new HashMap<List<String>, Integer>();
        result.put(s.subList(index, i + 1), Integer.valueOf(i));
        return result;
      }
      i += 1;
    }

    return new HashMap<List<String>, Integer>();

  }

  static public Map<List<String>, List<String>> backwardSlice(List<String> s) {
    int balance = 0;
    int L = s.size();
    int i = L - 1;

    while (i >= 0) {
      balance = s.get(i).equals(")") ? balance + 1 : s.get(i).equals("(") ? balance-1 : balance+0;
      if (balance == 0) {
        if (i > 0 && s.get(i - 1).equals("!")) {
          i -= 1;
        } else {
          i -= 0;
        }
        Map<List<String>, List<String>> result = new HashMap<List<String>, List<String>>();
        result.put(s.subList(i, L), s.subList(0, i));
        return result;
      }
      i -= 1;
    }

    return new HashMap<List<String>, List<String>>();
  }

  static public List<String> aroundUnaryOp(List<String> s, String op){
    List<String> pr = new ArrayList<String>();
    int i=0;
    List<String> sSlice;
    while (i < s.size()){
      if(s.get(i).equals(op)){
        i+=1;
        sSlice = forwardSlice(s, i).keySet().stream().findFirst().get();
        i = forwardSlice(s,i).values().stream().findFirst().get();
        sSlice = aroundUnaryOp(sSlice, op);
        pr.add("(");
        pr.add("!");
        for(String p:sSlice){pr.add(p);}
        pr.add(")");
      }else{
        pr.add(s.get(i));
      }
      i += 1;
    }
    return pr;
  }

  static public List<String> aroundBinaryOp(List<String> s, String op){
    List<String> pr = new ArrayList<String>();
    int i = 0;
    while(i < s.size()){
      if(s.get(i).equals(op)){
        List<String> A = backwardSlice(pr).keySet().stream().findFirst().get();
        pr = backwardSlice(pr).values().stream().findFirst().get();
        A = aroundBinaryOp(A, op);
        i+=1;
        List<String> sSlice = forwardSlice(s, i).keySet().stream().findFirst().get();
        i = forwardSlice(s, i).values().stream().findFirst().get();
        sSlice = aroundBinaryOp(sSlice, op);
        pr.add("(");
        for(String p:A){pr.add(p);}
        pr.add(op);
        for(String p:sSlice){pr.add(p);}
        pr.add(")");
      }else{
        pr.add(s.get(i));
      }
      i+=1;
    }
    return pr;
  }

  static public List<String> induceParenthesis(List<String> s){
    s = aroundUnaryOp(s, "!");
    s = aroundBinaryOp(s, "&");
    s = aroundBinaryOp(s, "|");
    s = aroundBinaryOp(s, ">");
    s = aroundBinaryOp(s, "=");
    return s;
  }

  static public boolean literalIsNotProtected(List<String> s){
    int counter = 0;
    for(String p:s){
      if(isOperator(p))
        counter++;
    }
    if(counter==0)
      return false;

    int balance = 0;
    for(int i=0; i<s.size(); i++){
      if(s.get(i).equals("(")){
        balance+=1;
      }else if(s.get(i).equals(")")){
        balance -= 1;
      }else if(balance == 0){
        return true;
      }
    }

    return false;
  }

  static public List<String> equivautConvertor(List<String> A, List<String> B) {
    List<String> result = new ArrayList<String>();
    result.add("(");
    result.add("(");
    for(String s : A){result.add(s);}
    result.add(">");
    for(String s : B){result.add(s);}
    result.add(")");
    result.add("&");
    result.add("(");
    for(String s : B){result.add(s);}
    result.add(">");
    for(String s : A){result.add(s);}
    result.add(")");
    result.add(")");
    return result;
  }

  static public List<String> impliqueConvertor(List<String> A, List<String> B){
    List<String> result = new ArrayList<String>();
    result.add("(");
    result.add("(");
    result.add("!");
    for(String s : A){result.add(s);}
    result.add(")");
    result.add("|");
    for(String s : B){result.add(s);}
    result.add(")");
    return result;
  }

  static public List<String> elimineOperator(List<String> s, String op){
    List<String> pr = new ArrayList<String>();
    int i=0;
    while(i<s.size()){
      if(s.get(i).equals(op)){
        List<String> A = backwardSlice(pr).keySet().stream().findFirst().get();
        pr = backwardSlice(pr).values().stream().findFirst().get();
        i+=1;
        List<String> B = forwardSlice(s, i).keySet().stream().findFirst().get();
        i = forwardSlice(s, i).values().stream().findFirst().get();
        A = elimineOperator(A, op);
        B = elimineOperator(B, op);
        if(op.equals("=")){
          for(String p:equivautConvertor(A, B)){pr.add(p);}
        }else{
          for(String p:impliqueConvertor(A,B)){pr.add(p);}
        }
      }else{
        pr.add(s.get(i));
      }
      i+=1; 
    }
    return pr;
  }

  static public List<String> moveNotInwards(List<String> s){
    List<String> pr = new ArrayList<String>();
    List<String> B = new ArrayList<String>();
    List<String> tmp = new ArrayList<String>();
    int i = 0;
    int j;
    while(true){
      while(i < s.size()){
        if(s.get(i).equals("!")){
          i += 1;
          B = forwardSlice(s, i).keySet().stream().findFirst().get();
          i = forwardSlice(s, i).values().stream().findFirst().get();
          if(B.get(0).equals("(")){
            pr.add("(");
            j = 1;
            while(j<B.size()){
              tmp = forwardSlice(B, j).keySet().stream().findFirst().get();
              j = forwardSlice(B, j).values().stream().findFirst().get();
              if(tmp.get(0).equals("!")){tmp.remove(0);}else{tmp.add(0, "!");}
              for(String k:tmp){pr.add(k);}
              j += 1;
              if(j<B.size()-1){
                if(B.get(j).equals("|")){
                  pr.add("&");
                }else{
                  if(B.get(j).equals("&"))
                    pr.add("|");
                }
              }
              j += 1;
            }
            pr.add(")");
          }else{
            if(B.get(0).equals("!")){
              B.remove(0);
            }else{
              pr.add("!");
              for(String k:B){pr.add(k);}
            }
          }
        }else{
          pr.add(s.get(i));
        }
        i+=1;
      }
      if(pr.equals(s)){break;}
      s = pr;
    }
    return pr;
  }

  static public List<String> distributeOrOverAnd(List<String> s){
    try(PythonInterpreter py = new PythonInterpreter()){
      py.exec("import sys");
      py.exec("import os");
      py.set("src", new PyString("src"));
      py.set("main", new PyString("main"));
      py.set("resources", new PyString("resources"));
      py.exec("sys.path.append(os.path.join(os.getcwd(),src,main,resources))");
      py.exec("from refutation import distribute_or_over_and");
      py.set("s", new PyList(s)); 
      py.exec("result = distribute_or_over_and(s)");
      List<String> result = new PyList(py.get("result"));
      return result;
    }
  }



  static public List<String> eliminateInvalidParenthesis(List<String> s){
    List<String> pr = new ArrayList<String>();
    List<String> brackets = new ArrayList<String>();
    List<String> content = new ArrayList<String>();
    
    for(int i=0; i<s.size(); i++){
      if(s.get(i).equals("(")){
        for(String k:pr){content.add(k);}
        brackets.add("(");
        pr.clear();
      }else if(s.get(i).equals(")") && content.size()>0){
        if(literalIsNotProtected(pr)){
          pr.add(0, "(");
          pr.add(pr.size()-1, ")"); 
        }
        pr.add(0, content.get(content.size()-1));
        brackets.remove(brackets.get(brackets.size()-1));
        content.remove(content.get(content.size()-1));
      }else{
        pr.add(s.get(i));
      }
    }
    return pr;
  }

  static public List<String> processOperand(List<String> s){
    s = eliminateInvalidParenthesis(s);
    List<String> result = new ArrayList<String>();
    if(s.get(0).equals("(")){
      result.add("(");
      for(String j:s){
        if(!j.equals("(") && !j.equals(")")){
          result.add(j);
        }
      }
      result.add(")");
      return result;
    }else{
      for(String j:s){
        if(!j.equals("(") && !j.equals(")")){
          result.add(j);
        }
      }
      return result;
    }
  }


  static public List<String> splitAroundAnd(List<String> s){
    List<String> pr = new ArrayList<String>();
    List<String> operand = new ArrayList<String>();
    List<String> result = new ArrayList<String>();

    for(int i=0; i<s.size(); i++){
      if(s.get(i).equals("&")){
        for(String k:processOperand(operand)){pr.add(k);}
        pr.add("&");
        operand.clear();
      }else{
        operand.add(s.get(i));
      }
    }

    for(String k:pr){result.add(k);}
    for(String k:processOperand(operand)){result.add(k);}

    return result;
  }

  static public List<String> cNF(List<String> s){
    s = elimineOperator(s, "=");
    s = eliminateInvalidParenthesis(s);
    s = elimineOperator(s, ">");
    s = eliminateInvalidParenthesis(s);
    s = moveNotInwards(s);
    s = eliminateInvalidParenthesis(s);

    List<String> prev = new ArrayList<String>();
    while(!prev.equals(s)){
      prev = s;
      s = distributeOrOverAnd(s);
      s = eliminateInvalidParenthesis(s);
    }

    return splitAroundAnd(s);
  } 

  static public HashMap<String, Boolean> clauseMap(List<String> s) {
    HashMap<String, Boolean> map = new HashMap<String, Boolean>();
    List<String> literal = new ArrayList<String>();
    int j,L;
    if(s.get(0).equals("(")){
      j = 1;
    }else{
      j=0;
    }

    if(s.get(0).equals("(")){
      L = s.size()-1;
    }else{
      L = s.size();
    }

    while (j<L) {
      literal = forwardSlice(s, j).keySet().stream().findFirst().get();
      j = forwardSlice(s, j).values().stream().findFirst().get();
      if(literal.get(0).equals("!")){
        map.put(literal.get(1), true);
      }else{
        map.put(literal.get(0), false);
      }
      j+=2;
    }

    return map;
  }

  static public Boolean all(List<HashMap<String,Boolean>> clause_map, List<HashMap<String,Boolean>> new_clause_map){
    for(HashMap<String,Boolean> clause: new_clause_map){
      if(!clause_map.contains(clause))
        return false;
    }
    return true;
  }

  static public boolean resolve(List<String> s, int debugActive){
    List<String> clause = new ArrayList<String>();
    List<String> clauses = new ArrayList<String>();
    List<HashMap<String, Boolean>> clause_map = new ArrayList<HashMap<String, Boolean>>();
    boolean m;
    if(debugActive>=1){m=true;}else{m=false;}


    for(String k:s){
      String complete="";
      if(k.equals("&")){
        for(String v:clause){
          complete += v;
        }
        clauses.add(complete);
        clause_map.add(clauseMap(clause));
        clause.clear();
      }else{
        clause.add(k);
      }
    }
    String complete = "";
    for (String v : clause) {
      complete += v;
    }
    clauses.add(complete);
    clause_map.add(clauseMap(clause));
    
    List<HashMap<String, Boolean>> new_clause_map = new ArrayList<HashMap<String, Boolean>>();

    if (m) {
      System.out.println("Clauses <- L'ensemble des clauses dans leur forme normale (CNF)");
      System.out.println(String.format("Clauses: %s", clauses));
      System.out.println("Nouvelle clauses <- %s");
      System.out.println("Parcours de toutes les pairs de clause dans le dictionnaire");
    }

    while (true) {
      for (int i=0; i<clause_map.size(); i++) {
        for (int j=i+1; j<clause_map.size(); j++) {
          HashMap<String, Boolean> resolvent = new HashMap<String, Boolean>();
          for (String var:clause_map.get(i).keySet()) {
            if(!clause_map.get(j).containsKey(var) || clause_map.get(j).get(var) == clause_map.get(i).get(var)){
              resolvent.put(var, clause_map.get(i).get(var));
            } 
          }

          for (String var : clause_map.get(j).keySet()) {
            if(!clause_map.get(i).containsKey(var))
              resolvent.put(var, clause_map.get(j).get(var));
          }

          if(m){
            System.out.println(String.format("\t(%s) <- RESOLVE((%s), (%s))", resolvent, clause_map.get(i), clause_map.get(j)));
          }else{}

          if(!Boolean.TRUE.equals(resolvent)){
            if(m){
              System.out.println("Si resolvent contient une clause vide: return True");
            }else{}
            return true;
          }

          if(!new_clause_map.contains(resolvent)){
            new_clause_map.add(resolvent);
          }else{}
          if(m){
            System.out.println("\tNouvelle clause <- nouvelle clause u resolvent");
          }else{}
        }
      }
      if(all(clause_map, new_clause_map)){
        if(m){System.out.println("If nouvelle liste clause inclu dans liste de clause : return False");}else{}
        return false;
      } 
      for(HashMap<String,Boolean> cl: new_clause_map){
        if(!clause_map.contains(cl)){
          clause_map.add(cl);
        }
      }
      if(m){
        System.out.println("clauses <- clauses + nouvelles clauses");
      }else{

      }
    }
  }

  static public List<String> vetSentence(List<String> s){
    s = induceParenthesis(s); 
    s = eliminateInvalidParenthesis(s);
    return cNF(s);
  }
  
  static public ArrayList<Object> getInput(){
    Scanner sc = new Scanner(System.in);
    String[] elements = sc.nextLine().split(" ");
    if(elements.length > 2){System.out.println("Vous avez entré un nombre incorrect d'arguments");System.exit(1);}
    Integer n = Integer.parseInt(elements[0]);
    Integer m = Integer.parseInt(elements[1]);

    List<String> sentences = new ArrayList<String>();
    while(n>0){
      sentences.add(sc.nextLine().split(" ")[0]);
      n-=1;
    }

    String query = sc.nextLine().split(" ")[0];

    return new ArrayList<Object>(){
      {
        add(m); 
        add(sentences); 
        add(query);
      }
    };
  }

  static public int solve(){
    List<Object> result = getInput();
    Integer m = (Integer)result.get(0);
    List<String> sentences = (List<String>)result.get(1);
    String query = (String)result.get(2);

    List<String> baseDeConnaissance = new ArrayList<String>();
    for (String sentence : sentences){
      List<String> Lsentence = vetSentence(segmentSentence(sentence));
      for(String k:Lsentence){baseDeConnaissance.add(k);}
      baseDeConnaissance.add("&");
    } 
    baseDeConnaissance.remove(baseDeConnaissance.size()-1);

    List<String> Lquery = vetSentence(segmentSentence(query));

    List<String> oppose = new ArrayList<String>();
    oppose.add("!");
    for (String k : Lquery) {
      oppose.add(k);
    }

    List<String> old = oppose;
    if(baseDeConnaissance.size() > 0){
      for (String k : baseDeConnaissance) {
        oppose.add(k);
      }
      oppose.add("&");
      oppose.add("(");
      for (String k : old) {
        oppose.add(k);
      }
      oppose.add(")");
    }

    if(oppose.size()!=0){
      oppose = vetSentence(oppose);
      resolve(oppose, m);
      return 0;
    }
    return 0;
  }
}

