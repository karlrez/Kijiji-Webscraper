/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.ImageLogic;

/**
 *
 * @author karlr
 */
@WebServlet(name = "ImageTable", urlPatterns = {"/ImageTable"})
public class ImageTableViewNormal extends HttpServlet {
    
    //Variables
    ImageLogic logic = new ImageLogic();
    Image aImage = new Image();
    List<Image> entityList = logic.getAll();

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
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/tablestyle.css\">");
            out.println("<title>Servlet ImageTableViewNormal</title>");            
            out.println("</head>");
            out.println("<body>");
            
            //Table
            out.println("<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">");
            out.println("<caption>Image</caption>");
            
            //Printing out table headers
            out.println("<tr>" + getTableHeaders() + "</tr>");
            
            //Printing out table data
            entityList.forEach((entity) -> {
                out.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                        logic.extractDataAsList(entity).toArray());
            });
            
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    //Method to return the table headers as a single String
    String getTableHeaders() {
        List<String> tableHeadersList = logic.getColumnNames();
        StringBuilder tableHeaders = new StringBuilder();
    
        tableHeadersList.forEach((header) -> {
            tableHeaders.append("<th>").append(header).append("</th>");
        });    
        return tableHeaders.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
