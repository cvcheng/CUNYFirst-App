package com.cunyfirst;

import android.app.AlertDialog;

public class CourseH {
        private String name;
        private String term;
        private String credit;
        private String grade;
        private String progress;
        private String institution;

        public CourseH(String name, String term, String credit, String grade, String progress, String institution) {
            this.name = name;
            this.term = term;
            this.credit = credit;
            this.grade = grade;
            this.progress = progress;
            this.institution = institution;
        }

        public String getName() {
            return name;
        }
        public String getTerm() {
            return term;
        }
        public String getCredit() {
            return credit;
        }
        public String getGrade() {
            return grade;
        }
        public String getProgress() {
            return progress;
        }
        public String getInstitution() {
            return institution;
        }
}
