/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Item;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CategoryLogic;
import logic.ImageLogic;
import logic.ItemLogic;

/**
 *
 * @author karlr
 */
@WebServlet(name = "CreateItem", urlPatterns = {"/CreateItem"})
public class CreateItem extends HttpServlet {
    
    private String errorMessage = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Feed</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1 align='center'>Create Item Table</h1>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            
            //Adding required and maxlength attribute to input tags where needed
            out.println("<form method=\"post\">");
            out.println("Item ID:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" required><br>",ItemLogic.ID);
            out.println("<br>");
            out.println("Image ID:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" required><br>",ItemLogic.IMAGE_ID);
            out.println("<br>");
            out.println("Category ID:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" required><br>",ItemLogic.CATEGORY_ID);
            out.println("<br>");
            out.println("Price:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",ItemLogic.PRICE);
            out.println("<br>");
            out.println("Title:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" maxlength='255' required><br>",ItemLogic.TITLE);
            out.println("<br>");
            out.println("Date:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" placeholder=\"dd/mm/yyyy\"><br>",ItemLogic.DATE);
            out.println("<br>");
            out.println("Location:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" maxlength='45'><br>",ItemLogic.LOCATION);
            out.println("<br>");
            out.println("Description:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" maxlength='255' required><br>",ItemLogic.DESCRIPTION);
            out.println("<br>");
            out.println("URL:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\" maxlength='255' required><br>",ItemLogic.URL);
            out.println("<br>");
            
            //Submit buttons
            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">");
            out.println("</form>");
            if(errorMessage!=null&&!errorMessage.isEmpty()){
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }           
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        
        ItemLogic itemLogic = new ItemLogic();
        CategoryLogic categoryLogic = new CategoryLogic();
        ImageLogic imageLogic = new ImageLogic();
        Item item = itemLogic.createEntity( request.getParameterMap());
        String url = request.getParameter(ItemLogic.URL);
        
        //First checking url is unique 
        if(itemLogic.getWithUrl(url)==null){    
            //then checking if Category and Image ID exist in db
            if (categoryLogic.getWithId(Integer.parseInt(request.getParameter(ItemLogic.CATEGORY_ID))) != null && 
                    imageLogic.getWithId(Integer.parseInt(request.getParameter(ItemLogic.IMAGE_ID))) != null) {
                //setting their values and adding Item to db
                item.setCategory(categoryLogic.getWithId(Integer.parseInt(request.getParameter(ItemLogic.CATEGORY_ID))));
                item.setImage(imageLogic.getWithId(Integer.parseInt(request.getParameter(ItemLogic.IMAGE_ID))));
                itemLogic.add(item);
            } else errorMessage = "Invalid- Category or Image do not exist!";
        }else{
            //if duplicate print the error message
            errorMessage = "URL: \"" + url + "\" already exists";
        }
        if( request.getParameter("add")!=null){
            //if add button is pressed return the same page
            processRequest(request, response);
        }else if (request.getParameter("view")!=null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("ItemTable");
        }
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Item Entity";
    }
    
    private static final boolean DEBUG = true;

    public void log( String msg) {
        if(DEBUG){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log( message);
        }
    }

    public void log( String msg, Throwable t) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log( message, t);
    }

}