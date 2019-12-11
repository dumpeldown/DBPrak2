public class Lieferschein{

    print(int bestnr){
        int knr = SQLManager.getKundennummerFromBest(best)
        SQLManager.getKunde(knr);
        int bstnr = SQLManager.getBstByBestnr(bestnr);
        SQLManager.getLagerbeständeByBstnr(bstnr);
        SQLManager.getBoxenByBst(bstnr);
    }
}
