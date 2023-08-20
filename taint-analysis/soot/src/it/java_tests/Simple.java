/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
import java.util.*;
public class Simple {

    private int x;
    
    public static void main(String[] args) {
        int j = 1;
        int i = 2;;
        if ( i + j > 1) {
            System.out.println("Hello");
        }
        for (int s = 0; s < 10; s++) { j = j + 1; }
        for (int t = 0; t < 10; t--) { j = j + 1; }
        System.out.println("hello");

        Simple.add(1,2);
        Simple simple = new Simple();
        Simple.getString().toString();
        System.out.println(Simple.add(2,3));
        simple.run();
    }
   
    public static String getString(){ return "Hello";}
    public static int add(int i, int j) { return i + j; }
    
    public Simple() { 
        //super(); 
    
        this (8);
    }
    public Simple(int y ) {
        this.x = y;
        System.out.println(this.x);
    }

    public void run(){

        int i = 9;
        int j = 10;
        j += i;
        System.out.println(j);
        int k = +(i + j);
        int l = +k;
        double m = +0.9;
   
        int n = ~j;

        boolean b = true;
        boolean c = false;
        boolean d = true;
        if (b && c && d) {
            System.out.println("Cond_and");
        }
        if (b || c) {
            System.out.println("Cond_or");
        }
        
        if (b & c) {
            System.out.println("Bit_and");
        }
        if (b | c) {
            System.out.println("Bit_or");
        }
        if (b ^ c) {
            System.out.println("Bit_Xor");
        }
        boolean x = true;

        String y = x ? "Smile" : "Frown";
        System.out.println(y);

        if (!x) {
            throw new RuntimeException();
        }
        int a = 9;
        int b1 = 8;
        int [][] intArr = new int [a+b1][9];
        
        int [] arr = {1 , 2, 3};
        System.out.println(arr[0]);
        Integer int1 = new Integer(8);
        ArrayList list = new ArrayList();
        Object o1 = new Integer(8);
        Object o2 = new Integer(9);
        list.add(o1);
        list.add(o2);

        Iterator it = list.iterator();
        while (it.hasNext()){
            Integer intRep = (Integer)it.next();
            System.out.println(intRep.intValue());
        }
        
        int i2 = 4;
        while (i2 < 10 ) {
            System.out.println(i2);
            i2 = i2 + 2;
        }
    }
}

