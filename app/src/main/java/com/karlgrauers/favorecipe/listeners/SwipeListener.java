package com.karlgrauers.favorecipe.listeners;
import android.view.GestureDetector;
import android.view.MotionEvent;

/*
 * Klass för att hantera swipe-gester. Används i nuläget endast
 * i klass 'RecipeSearchFragment' för att låta användare swipea
 * fram eller bort delar av sökcontainern.
 * Implementationen är lånad från tråd på StackOverFlow: https://stackoverflow.com/questions/13095494/how-to-detect-swipe-direction-between-left-right-and-up-down
 */




public class SwipeListener extends GestureDetector.SimpleOnGestureListener {


    /**
     * Fånga motionevent och beräkna åt vilket håll användare swipear.
     * @param e1 innehåller MotionEvent där swipe påbörjades.
     * @param e2 innehåller MotionEvent där swipe avslutades.
     * @param velocityX innehåller hastighet för vertikal swipe
     * @param velocityY innehåller hastighet för horisontell swipe
     * @return kall till metod onSwipe med swiperiktningen som argument.
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Två motionevent används, e1 och e2. e1 är det intiala eventet, alltså
        // där användares finger påbörjar swipe. e2 är sedan event där swipe avslutas.
        // e2 kan finnas i fyra olika områden  relativt till e1 (A, B, C eller D i diagrammet nedan).
        //
        //         \ A  /
        //          \  /
        //       D   e1   B
        //          /  \
        //         / C  \
        //
        // Om x2 och y2 (se variabler nedan) är i:
        //  A => swipear UPP
        //  B => swipear NER
        //  C => swipear HÖGER
        //  D => swipear VÄNSTER
        //
        //OBS! Endast upp och ner swipe används i nuläget.

        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1,y1,x2,y2);
        return onSwipe(direction);
    }

    /**
     * Metod som överskuggas i implementerande klass.
     * Det här som definieras vad som ska ske vid swipe i olika
     * riktningar.
     * @param direction innehåller riktningen på swipe
     *                  (kommer från enum 'Direction')
     */
    public boolean onSwipe(Direction direction){
        return false;
    }



    /**
     * Beräkna riktning utifrån de swipe-punkter som
     * sätts i metod 'onFling'.
     * @param x1 x-position för första punkt (alltså där swipe påbörjades)
     * @param y1 y-position för första punkt (alltså där swipe avslutades)
     * @param x2 x-position för andra punkt (alltså där swipe påbörjades)
     * @param y1 y-position för andra punkt (alltså där swipe avslutades)
     * @return riktningen på swipe
     */
    public Direction getDirection(float x1, float y1, float x2, float y2){
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.fromAngle(angle);
    }



    /**
     * Beräkna vinkel mellan swipe-punkterna satta i metod 'onFling'.
     * Beräknar vinkel genom att "placera ut" swipe-punkterna
     * i en 360 graders cirkel, och mäta avstånd mellan x1 och x2 respektive y1 och y2.
     *
     * @param x1 x-position för första punkt (alltså där swipe påbörjades)
     * @param y1 y-position för första punkt (alltså där swipe avslutades)
     * @param x2 x-position för andra punkt (alltså där swipe påbörjades)
     * @param y1 y-position för andra punkt (alltså där swipe avslutades)
     * @return vinkeln på swipe
     */
    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
        return (rad*180/Math.PI + 180)%360;
    }




    public enum Direction{
        up,
        down,
        left,
        right;

        /**
         * Returnera riktning givet vinkeln.
         * Riktningar definierade enligt följande:
         *
         * Upp: 45, 135
         * Höger: 0,45 och 315, 360
         * Ner: 225, 315
         * Vänster: 135, 225
         *
         * @param angle innehåller vinkel 0 till 360 grader
         * @return riktningen på vinkel
         */

        public static Direction fromAngle(double angle){
            if(inRange(angle, 45, 135)){
                return Direction.up;
            }
            else if(inRange(angle, 0,45) || inRange(angle, 315, 360)){
                return Direction.right;
            }
            else if(inRange(angle, 225, 315)){
                return Direction.down;
            }
            else{
                return Direction.left;
            }

        }

        /**
         * @param angle vinkeln
         * @param init initialt värde
         * @param end slutligt värde
         * @return return true om vinkeln är inom initialt och slutligt värde, annars false
         */
        private static boolean inRange(double angle, float init, float end){
            return (angle >= init) && (angle < end);
        }
    }

}
