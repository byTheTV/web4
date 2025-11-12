package org.example.servlets;

import java.io.IOException;

import org.example.models.Params;
import org.example.models.Result;
import org.example.models.ResultsBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }
    

    private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long startTime = System.nanoTime();
        
        Params params = new Params(req.getParameterMap());
        boolean hit = calculate(params.getX(), params.getY(), params.getR());

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        String executionTimeStr = String.format("%.3f мс", executionTime / 1_000_000.0);

        HttpSession session = req.getSession(true);
        ResultsBean resultsBean = (ResultsBean) session.getAttribute("resultsBean");
        if (resultsBean == null) {
            resultsBean = new ResultsBean();
            session.setAttribute("resultsBean", resultsBean);
        }
        resultsBean.add(new Result(executionTimeStr, params.getX(), params.getY(), params.getR(), hit));

        req.setAttribute("x", params.getX());
        req.setAttribute("y", params.getY());
        req.setAttribute("r", params.getR());
        req.setAttribute("hit", hit);
        req.getRequestDispatcher("/result.jsp").forward(req, resp);
    }

    private boolean calculate(int x, double y, double r) {
        if (x >= 0 && y >= 0) {
            return (x * x + y * y) <= (r / 2) * (r / 2);
        }
        if (x <= 0 && y >= 0) {
            return x >= -r / 2 && y <= r;
        }
        if (x >= 0 && y <= 0) {
            return x >= -r && y >= -r / 2 && y >= (x / 2.0 - r / 2.0);
        }
        return false;
    }
}


