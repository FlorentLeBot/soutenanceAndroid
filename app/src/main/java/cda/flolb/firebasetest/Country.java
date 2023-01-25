package cda.flolb.firebasetest;

import java.util.List;

public class Country {
    private List<String> flags;
    private String flag;
    private Translations translations;

    public List<String> getFlags() {
        return flags;
    }

    public String getFlag() {
        return flag;
    }

    public Translations getTranslations() {
        return translations;
    }

    public class Translations {
        private French fra;

        public French getFra() {
            return fra;
        }

        public class French {
            private String common;

            public String getCommon() {
                return common;
            }
        }
    }
}




