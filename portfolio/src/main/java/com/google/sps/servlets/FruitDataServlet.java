package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/fruit-data")
public class FruitDataServlet extends HttpServlet {

    private Map<String, Integer> fruitVotes = new HashMap<>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(fruitVotes);
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fruit = request.getParameter("fruit");
        int currentVotes = fruitVotes.containsKey(fruit) ? fruitVotes.get(fruit) : 0;
        fruitVotes.put(fruit, currentVotes + 1);

       response.sendRedirect("/index.html");
    }
}   