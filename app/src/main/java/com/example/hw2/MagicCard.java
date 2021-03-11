package com.example.hw2;

import org.json.JSONException;
import org.json.JSONObject;

public class MagicCard {
    private String name;
    private String type;
    private String rarity;
    private String colors ="-";  //anscheinend nicht immer vorhanden

    public MagicCard(String name, String type, String rarity, String colors) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.colors = colors;
    }

    public MagicCard(JSONObject result) {
        try{
            this.name = result.getString("name");
            this.type = result.getString("type");
            this.rarity = result.getString("rarity");
            if (result.getJSONArray("colors") != null){
                this.colors = result.getJSONArray("colors").toString();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getCardData(){
        if (colors == "-")
            return name + " \n-Type: " + type + "\n-rarity: " + rarity + "\n\n";
        else
            return name + " " + colors + " \n-Type: " + type + "\n-rarity: " + rarity + "\n\n";
    }

}
