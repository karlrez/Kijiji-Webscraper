/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import entity.Category;
import entity.Image;
import entity.Item;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CategoryLogic;
import logic.ImageLogic;
import logic.ItemLogic;
import scraper.kijiji.Kijiji;

/**
 *
 * @author karlr
 */
@WebServlet(name = "Kijiji", urlPatterns = {"/Kijiji"})
public class KijijiView extends HttpServlet {

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
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/KijijiStyle.css\">");
            out.println("<title>Servlet KijijiPage</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet KijijiView</h1>");
            out.println("<div class='center-column'>"); //div for page
            
            ItemLogic itemLogic = new ItemLogic();
            List<Item> itemList = itemLogic.getAll();
            for (Item item : itemList) {
                out.println("<div class='item'>"); //div for item
                
                out.println("<div class='image'>"); //div for image
                out.printf("<img src=\"image/%d.jpg\" style=\"max-width: 250px; max-height: 200px;\" alt=\"image unavailable\"/>", item.getId());
                out.println("</div>"); //closing div for image
                
                out.printf("<span class='price'>Price: %s </span><br>", item.getPrice().toString());
                out.printf("<a class='title' href=\"%s\" target=\"_blank\">%s</a>", item.getUrl(), item.getTitle());               
                out.printf("<span class='date'>Date: %s </span><br><br>", item.getDate().toString());
                out.printf("<span class='location'>Location: %s </span><br>", item.getLocation());
                out.printf("<span class='description'>Description: %s </span></br>", item.getDescription());
                
                out.println("</div>"); //closing div for item
                out.println("<br><br>");
            }
            out.println("</div>"); //closing div for page
            out.println("</body>");
            out.println("</html>");
        }
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
       
        //creating new folder for images - only needed on first run 
        //new File(System.getProperty("user.home")+"/KijijiImages/").mkdir(); 
        String dir = System.getProperty("user.home")+"/KijijiImages/";
        
        //creating Kijiji and Logic classes 
        Kijiji kijiji = new Kijiji();
        ItemLogic itemLogic = new ItemLogic();
        ImageLogic imageLogic = new ImageLogic();
        Category category = new CategoryLogic().getWithId(2);
        
        //getting info from kijiji website
        kijiji.downloadPage(category.getUrl());
        kijiji = kijiji.findAllItems();
        
        kijiji.processItems((kijijiItem) -> {   
            //Checking if Item already exists in db and for not null conditions
            if (itemLogic.getWithId(Integer.parseInt(kijijiItem.getId())) != null
                    || kijijiItem.getTitle() == null
                    || kijijiItem.getDescription() == null
                    || kijijiItem.getUrl() == null
                    || kijijiItem.getImageName() == null
                    || kijijiItem.getImageUrl() == null) 
                return; //just leaves method
            
            //Check if image already in db before we save to directory
            if (imageLogic.getWithUrl(kijijiItem.getImageUrl()).isEmpty())
                FileUtility.downloadAndSaveFile(kijijiItem.getImageUrl(), dir, kijijiItem.getId() + ".jpg");
            
            //Adding image to db if its not already there
            Image image = imageLogic.getWithPath(dir + kijijiItem.getId() + ".jpg");
            if (image == null) {
                Map<String, String[]> imageParameterMap = new HashMap<>();
                imageParameterMap.put(ImageLogic.NAME, new String[]{kijijiItem.getImageName()});
                imageParameterMap.put(ImageLogic.PATH, new String[]{dir + kijijiItem.getId() + ".jpg"});
                imageParameterMap.put(ImageLogic.URL, new String[]{kijijiItem.getImageUrl()});
                image = imageLogic.createEntity(imageParameterMap);
                imageLogic.add(image);
            }
                //Adding Item to db
                Map<String, String[]> itemParameterMap = new HashMap<>();
                itemParameterMap.put(ItemLogic.ID, new String[]{kijijiItem.getId()});
                itemParameterMap.put(ItemLogic.PRICE, new String[]{kijijiItem.getPrice()});
                itemParameterMap.put(ItemLogic.TITLE, new String[]{kijijiItem.getTitle()});
                itemParameterMap.put(ItemLogic.DATE, new String[]{kijijiItem.getDate()});
                itemParameterMap.put(ItemLogic.LOCATION, new String[]{kijijiItem.getLocation()});
                itemParameterMap.put(ItemLogic.DESCRIPTION, new String[]{kijijiItem.getDescription()});
                itemParameterMap.put(ItemLogic.URL, new String[]{kijijiItem.getUrl()});
                Item item = itemLogic.createEntity(itemParameterMap);
                item.setImage(image);
                item.setCategory(category);
                itemLogic.add(item);
        });
        
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
