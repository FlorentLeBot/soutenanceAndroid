package cda.flolb.firebasetest;

import java.util.List;

/**
 * La classe Country représente un pays.
 * Elle contient des informations sur les drapeaux, la traduction et les
 */

public class Country {

    private List<String> flags;
    private String flag;
    private Translations translations;

    /**
     * Retourne la liste des drapeaux du pays
     * @return Liste des drapeaux
     */
    public List<String> getFlags() {
        return flags;
    }

    /**
     * Retourne le drapeau actuel du pays
     * @return Drapeau actuel
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Retourne les traductions du pays
     * @return Traductions
     */
    public Translations getTranslations() {
        return translations;
    }

    /**
     * La classe interne Translations contient les traductions d'un pays
     */
    public class Translations {

        private French fra;

        /**
         * Retourne la traduction française du pays
         * @return Traduction française
         */
        public French getFra() {
            return fra;
        }

        /**
         * La classe interne French contient les informations relatives à la traduction française d'un pays
         */
        public class French {

            private String common;

            /**
             * Retourne le nom commun du pays en français
             * @return Nom commun en français
             */
            public String getCommon() {
                return common;
            }
        }
    }
}





