package com.pensum.pensumapplication.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by eddietseng on 8/21/16.
 */
@ParseClassName("Skill")
public class Skill extends ParseObject {
    public Skill() {
    }

    public void setSkillName(String skillName) {
        put("skillName",skillName);
    }

    public void setSkillExperiences(int skillExperience) {
        put("skillExperience", skillExperience);
    }

    public String getSkillName() {
        return getString("skillName");
    }

    public int getSkillExperiences() {
        return getInt("skillExperience");
    }
}
