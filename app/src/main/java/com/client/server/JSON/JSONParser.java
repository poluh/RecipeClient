package com.client.server.JSON;

import android.graphics.Point;


import java.util.List;
import java.util.Map;

public class JSON {
    private List<Point> points;

    public JSON(List<Point> points) {
        this.points = points;
    }

    private JSONObject toJSONFormat() {
        JSONObject<String, JSONArray> mainJSON = new JSONObject<>();
        JSONArray allPointsArray = new JSONArray();

        for (Point point : points) {
            JSONObject<String, Integer> JSONPoint = new JSONObject<>();
            JSONPoint.put("x", point.x);
            JSONPoint.put("y", point.y);
            System.out.println(JSONPoint.toString());
            allPointsArray.put(JSONPoint);
        }
        mainJSON.put("allPoints", allPointsArray);
        System.out.println(points.size());
        return mainJSON;
    }

    public String getJSONString() {
        return toJSONFormat().toString();
    }
}
