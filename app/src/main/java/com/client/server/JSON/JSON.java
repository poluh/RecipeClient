package com.client.server;

import android.graphics.Point;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class JSON {
    private List<Point> points;

    public JSON(List<Point> points) {
        this.points = points;
    }

    private JSONObject toJSONFormat() throws JSONException {
        JSONObject mainJSON = new JSONObject();
        JSONArray allPointsArray = new JSONArray();

        for (Point point : points) {
            JSONObject JSONPoint = new JSONObject();
            JSONPoint.put("x", point.x);
            JSONPoint.put("y", point.y);
            allPointsArray.put(JSONPoint);
        }
        mainJSON.put("allPoints", allPointsArray);
        System.out.println(points.size());
        return mainJSON;
    }

    public String getJSONString() {
        try {
            return toJSONFormat().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
