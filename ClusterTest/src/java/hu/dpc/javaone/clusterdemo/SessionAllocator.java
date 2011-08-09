/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.dpc.javaone.clusterdemo;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author vrg
 */
public class SessionAllocator extends HttpServlet {

    private byte[] load;
    private static long counter;
    private int mBytes = 10;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init() throws ServletException {
        load = new byte[mBytes * 1024 * 1024];
        Random r = new Random();
        for (int i = 0; i < load.length; i++) {
            load[i] = (byte) r.nextInt();
        }
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            HttpSession session = request.getSession();

            if (session.isNew()) {
                session.setAttribute("largeData", load);
                session.setAttribute("id", ++counter);
            }
            
            String queryString = request.getQueryString();
            
            String requestAsString = request.getRequestURI();
            if (queryString != null) {
                requestAsString += "?" + queryString;
            }

            out.println("allocated.megabytes=" + mBytes);
            out.println("session.id=" + session.getId());
            out.println("id=" + session.getAttribute("id"));
            out.println("session.created=" + sdf.format(new Date(session.getCreationTime())));
            out.println("all.sessions.created=" + counter);
            out.println("link=" + response.encodeURL(requestAsString));
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
