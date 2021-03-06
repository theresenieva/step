// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        int limit = getCommentLimit(request);
        
        ArrayList<Comment> messages = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            long timestamp = (long) entity.getProperty("timestamp");
            String text = (String) entity.getProperty("text");

            Comment comment = new Comment(id, name, timestamp, text);
            messages.add(comment);
            limit--;
            if (limit == 0) {
                break;
            }
        }

        Gson gson = new Gson();

        // Send the JSON as the response
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(messages));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the input from the form.
        String text = getParameter(request, "text-input", "");
        String name = getParameter(request, "name-input", "No Name");
        long timestamp = System.currentTimeMillis();
        boolean upperCase = Boolean.parseBoolean(getParameter(request, "upper-case", "false"));
      
        // Convert the text to upper case.
        if (upperCase) {
            text = text.toUpperCase();
        }

        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("text", text);
        commentEntity.setProperty("name", name);
        commentEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        response.sendRedirect("/index.html");
    }

    /** Converts messages to Json */
    private String convertMessagesToJson(ArrayList<String> messages) {
        String json = "{";
        json += "\"Messages\": ";
        json += "[ ";

        for (int i = 0; i < messages.size(); i++) {
            json += "\"" + messages.get(i) + "\"";
            if (i < messages.size() - 1) {
                json += ", ";
            }
        }
        json += "]";
        json += "}";      
        return json;
    }

    /**
     * @return the request parameter, or the default value if the parameter
     *         was not specified by the client
     */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /** 
     * Get comment limit from query string
     */
    private int getCommentLimit(HttpServletRequest request) {
        String queryString = request.getQueryString();

        String[] parts = queryString.split("=");
        return Integer.parseInt(parts[1]);
    }
}