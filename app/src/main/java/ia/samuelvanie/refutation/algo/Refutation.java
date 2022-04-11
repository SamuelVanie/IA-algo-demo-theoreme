package ia.samuelvanie.refutation.algo;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Refutation {
  static final List<String> POSSIBLE_OPERATORS = Arrays.asList(new String[] { "!", "&", "|", ">", "=", "(", ")" });

  static public boolean isOperator(String op) {
    return POSSIBLE_OPERATORS.contains(op);
  }

  static public List<String> tokenList(String s){
  	ArrayList<String> liste = new ArrayList<String>();
  	String arrayS[] = s.split("");
  	liste = (ArrayList<String>)Arrays.asList(arrayS);
  	return liste;
  }

  static public ArrayList<String> segmentSentence(String s) {
    ArrayList<String> segmentedSentences = new ArrayList<String>();
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

  static public Map<ArrayList<String>, Integer> forwardSlice(ArrayList<String> s, Integer index) {
    int balance = 0;
    int i = index;

    while (i < s.size()) {
      balance = s.get(i).equals("(") ? balance + 1
          : s.get(i).equals(")") ? balance-1 : balance;
      if (balance == 0 && !(s.get(i).equals("!"))) {
        Map<ArrayList<String>, Integer> result = new HashMap<ArrayList<String>, Integer>();
        result.put(new ArrayList<String>(s.subList(index, i + 1)), Integer.valueOf(i));
        return result;
      }
      i += 1;
    }

    return new HashMap<ArrayList<String>, Integer>();

  }

  static public Map<ArrayList<String>, ArrayList<String>> backwardSlice(ArrayList<String> s) {
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
        Map<ArrayList<String>, ArrayList<String>> result = new HashMap<ArrayList<String>, ArrayList<String>>();
        result.put(new ArrayList<String>(s.subList(i, L)), new ArrayList<String>(s.subList(0, i)));
        return result;
      }
      i -= 1;
    }

    return new HashMap<ArrayList<String>, ArrayList<String>>();
  }

  static public ArrayList<String> aroundUnaryOp(ArrayList<String> s, String op){
    ArrayList<String> pr = new ArrayList<String>();
    int i=0;
    ArrayList<String> sSlice;
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

  static public ArrayList<String> aroundBinaryOp(ArrayList<String> s, String op){
    ArrayList<String> pr = new ArrayList<String>();
    int i = 0;
    while(i < s.size()){
      if(s.get(i).equals(op)){
        ArrayList<String> A = backwardSlice(pr).keySet().stream().findFirst().get();
        pr = backwardSlice(pr).values().stream().findFirst().get();
        A = aroundBinaryOp(A, op);
        i+=1;
        ArrayList<String> sSlice = forwardSlice(s, i).keySet().stream().findFirst().get();
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

  static public ArrayList<String> induceParenthesis(ArrayList<String> s){
    s = aroundUnaryOp(s, "!");
    s = aroundBinaryOp(s, "&");
    s = aroundBinaryOp(s, "|");
    s = aroundBinaryOp(s, ">");
    s = aroundBinaryOp(s, "=");
    return s;
  }

  static public boolean literalIsNotProtected(ArrayList<String> s){
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

  static public ArrayList<String> equivautConvertor(ArrayList<String> A, ArrayList<String> B) {
    ArrayList<String> result = new ArrayList<String>();
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

  static public ArrayList<String> impliqueConvertor(ArrayList<String> A, ArrayList<String> B){
    ArrayList<String> result = new ArrayList<String>();
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

  static public ArrayList<String> elimineOperator(ArrayList<String> s, String op){
    ArrayList<String> pr = new ArrayList<String>();
    int i=0;
    while(i<s.size()){
      if(s.get(i).equals(op)){
        ArrayList<String> A = backwardSlice(pr).keySet().stream().findFirst().get();
        pr = backwardSlice(pr).values().stream().findFirst().get();
        i+=1;
        ArrayList<String> B = forwardSlice(s, i).keySet().stream().findFirst().get();
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

  static public ArrayList<String> moveNotInwards(ArrayList<String> s){
    s = new ArrayList<String>(s);
    ArrayList<String> pr = new ArrayList<String>();
    ArrayList<String> B = new ArrayList<String>();
    ArrayList<String> tmp = new ArrayList<String>();
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

  static public ArrayList<String> distributeOrOverAnd(ArrayList<String> s){
    ArrayList<String> processedSentence = new ArrayList<String>();
    ArrayList<String> A = new ArrayList<String>();
    ArrayList<String> B = new ArrayList<String>();
    int i=0;
    while(i<s.size()){
      if(s.get(i).equals("|")){
        A = backwardSlice(processedSentence).keySet().stream().findFirst().get();
        processedSentence = backwardSlice(processedSentence).values().stream().findFirst().get();
        A = distributeOrOverAnd(A);
        ArrayList<ArrayList<String>> tmp3 = new ArrayList<ArrayList<String>>();
        ArrayList<String> tmp = new ArrayList<String>();

        if(A.get(0).equals("(")){
          int j = 1;
          while(j<A.size()-1){
            tmp = forwardSlice(A, j).keySet().stream().findFirst().get();
            j = forwardSlice(A, j).values().stream().findFirst().get();
            tmp3.add(tmp);
            j+=2;
          }
        }else{
          tmp3.add(A);
        }

        i+=1;

        assert i < s.size();

        B = forwardSlice(s, i).keySet().stream().findFirst().get();
        i = forwardSlice(s, i).values().stream().findFirst().get();
        B = distributeOrOverAnd(B);

        ArrayList<ArrayList<String>> tmp2 = new ArrayList<ArrayList<String>>();
        if(B.get(0).equals("(")){
          int j = 1;
          while(j<B.size()-1){
            tmp = forwardSlice(B, j).keySet().stream().findFirst().get();
            j = forwardSlice(B, j).values().stream().findFirst().get();
            tmp2.add(tmp);
            j+=2;
          }
        }else{
          tmp2.add(B);
        }

        for (int k=0;k<tmp2.size();k++ ) {
         for (int m=0; m<tmp3.size();m++ ) {
          processedSentence.add("("); 
          processedSentence.addAll((ArrayList<String>)tmp3.get(m).clone());
          processedSentence.add("|");
          processedSentence.addAll((ArrayList<String>)tmp2.get(k).clone());
          processedSentence.add(")");
          if(m!=tmp3.size()-1){
            processedSentence.add("&");
          }
         } 
         if(k!=tmp2.size()-1){
           processedSentence.add("&");
         }
        }
      }else{
        processedSentence.add(s.get(i));
      }
      i+=1;
    }
    return processedSentence;
  }



  static public ArrayList<String> eliminateInvalidParenthesis(ArrayList<String> s){
    ArrayList<String> processedSentence = new ArrayList<String>();
    ArrayList<String> brackets = new ArrayList<String>();
    ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();

    for(int i=0; i<s.size(); i++){
      if(s.get(i).equals("(")){
        content.add((ArrayList<String>)processedSentence.clone());
        brackets.add("(");
        processedSentence.clear();
      }else if(s.get(i).equals(")") && content.size()>=1){
        if(literalIsNotProtected(processedSentence)){
          processedSentence.add(0, "(");
          processedSentence.add(processedSentence.size(), ")");
        }
        processedSentence.addAll(0, (ArrayList<String>)content.get(content.size()-1).clone());
        brackets.remove(brackets.size()-1);
        content.remove(content.size()-1);
      }else{
        processedSentence.add(s.get(i));
      }
    }
    return processedSentence;  
  }

  static public ArrayList<String> processOperand(ArrayList<String> s){
    s = eliminateInvalidParenthesis(s);
    ArrayList<String> result = new ArrayList<String>();
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


  static public ArrayList<String> splitAroundAnd(ArrayList<String> s){
    ArrayList<String> pr = new ArrayList<String>();
    ArrayList<String> operand = new ArrayList<String>();
    ArrayList<String> result = new ArrayList<String>();

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

  static public ArrayList<String> cNF(ArrayList<String> s){
    s = elimineOperator(s, "=");
    s = eliminateInvalidParenthesis(s); 
    s = elimineOperator(s, ">");
    s = eliminateInvalidParenthesis(s);
    s = moveNotInwards(s);
    s = eliminateInvalidParenthesis(s);

    ArrayList<String> prev = new ArrayList<String>();
    while(!prev.equals(s)){
      prev = s;
      s = distributeOrOverAnd(s);
      s = eliminateInvalidParenthesis(s);
    }

    return splitAroundAnd(s);
  } 

  static public HashMap<String, Boolean> clauseMap(ArrayList<String> s) {
    HashMap<String, Boolean> map = new HashMap<String, Boolean>();
    ArrayList<String> literal = new ArrayList<String>();
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

  static public Boolean all(ArrayList<HashMap<String,Boolean>> clause_map, ArrayList<HashMap<String,Boolean>> new_clause_map){
    for(HashMap<String,Boolean> clause: new_clause_map){
      if(!clause_map.contains(clause))
        return false;
    }
    return true;
  }

  static public ArrayList<String> convertToLogic(HashMap<String,Boolean> clause){
    ArrayList<String> result = new ArrayList<String>();
    for (Map.Entry<String,Boolean> entry : clause.entrySet()) {
      if(entry.getValue()){
        result.add("non " + entry.getKey());
      }else{
        result.add(entry.getKey());
      } 
    }
    return result;
  }

  static public boolean resolve(ArrayList<String> s, int debugActive){
    ArrayList<String> clause = new ArrayList<String>();
    ArrayList<String> clauses = new ArrayList<String>();
    ArrayList<HashMap<String, Boolean>> clause_map = new ArrayList<HashMap<String, Boolean>>();
    boolean m;

    try {
      FileOutputStream fos = new FileOutputStream("result");
      PrintStream out = new PrintStream(fos);
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
      
      ArrayList<HashMap<String, Boolean>> new_clause_map = new ArrayList<HashMap<String, Boolean>>();

      if (m) {
        out.println("On converti l'ensemble des clauses dans leur forme normale (CNF)");
        out.println("Ajout de la négation de la clause à vérifier");
        out.println(String.format("Clauses: %s", clauses));
        out.println("Nouvelles clauses <- {}");
        out.println("Parcours de toutes les paires de clause de la base de connaissance");
      }

      while (true) {
        for (int i=0; i<clause_map.size(); i++) {
          for (int j=i+1; j<clause_map.size(); j++) {
            HashMap<String, Boolean> resolvent = new HashMap<String, Boolean>();
            for (String var:clause_map.get(i).keySet()) {
              if(!clause_map.get(j).containsKey(var) || clause_map.get(j).get(var).equals(clause_map.get(i).get(var))){
                resolvent.put(var, clause_map.get(i).get(var));
              } 
            }

            for (String var : clause_map.get(j).keySet()) {
              if(!clause_map.get(i).containsKey(var))
                resolvent.put(var, clause_map.get(j).get(var));
            }

            if(m){
              out.println(String.format("\t(%s) est obtenu à partir de %s et %s", convertToLogic(resolvent), convertToLogic(clause_map.get(i)), convertToLogic(clause_map.get(j))));
            }else{}

            if(resolvent.isEmpty()){
                out.println("On obtient un ensemble vide de clause, donc la propriété est vraie");
              out.close();
              return true;
            }

            if(!new_clause_map.contains(resolvent)){
              new_clause_map.add(resolvent);
            }else{}
            if(m){
              out.println(String.format("\tOn ajoute la clause %s obtenue à notre ensemble de clauses", convertToLogic(resolvent)));
            }else{}
          }
        }
        if(all(clause_map, new_clause_map)){
          out.println("Verifions si le nouvel ensemble de clauses obtenues est identique ou inclu dans celui de la base connaissance, si oui alors la propriété qu'on cherche à vérifier est fausse");
          out.close();
          return false;
        } 
        for(HashMap<String,Boolean> cl: new_clause_map){
          if(!clause_map.contains(cl)){
            clause_map.add(cl);
          }
        }
        if(m){
          out.println("On ajoute le nouvelle ensemble de clause obtenu à la base de connaissance");
        }else{}
      }
    } catch(Exception e){
      return false;
    }

    
  }

  static public ArrayList<String> vetSentence(ArrayList<String> s){
    s = induceParenthesis(s); 
    s = eliminateInvalidParenthesis(s);
    return cNF(s);

  }
  

  static public int solve(Integer m, ArrayList<String> sentences, String query){

    ArrayList<String> baseDeConnaissance = new ArrayList<String>();
    for (String sentence : sentences){
      ArrayList<String> Lsentence = vetSentence(segmentSentence(sentence));
      for(String k:Lsentence){baseDeConnaissance.add(k);}
      baseDeConnaissance.add("&");
    } 
    baseDeConnaissance.remove(baseDeConnaissance.size()-1);

    ArrayList<String> Lquery = vetSentence(segmentSentence(query));

    ArrayList<String> oppose = new ArrayList<String>();
    oppose.add("!");
    for (String k : Lquery) {
      oppose.add(k);
    }

    ArrayList<String> old = new ArrayList<String>(oppose);
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

