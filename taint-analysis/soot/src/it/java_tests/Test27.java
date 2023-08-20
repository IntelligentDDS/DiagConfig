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
public class Test27 {

    private int x = 9;

    private void go(){
        System.out.println("hi");
    }
    
    public static void main (String [] args){
        Test27 t = new Test27();
        t.run();
    }

    public void run(){
        TestInner1 t = new TestInner1();
        t.run();
    }
    
    class TestInner1 {

        private int y = 9;
        public void run(){
            System.out.println("Smile: Inner1: "+x);
            go();
            TestInner2 t2 = new TestInner2();
            t2.run();
        }

        class TestInner2 {
            public void run(){
                System.out.println("Smile: Inner2: "+x+y);
                go();
            }
        
        }
        
        
        
    }

}
