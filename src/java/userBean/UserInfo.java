package userBean;

import com.google.gson.Gson;
import dao.SelectQueryDao;
import dbConnection.conRs;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo extends HttpServlet {

    private String userId;
    private String columnName;
    private String tableName;
    private String whereCondition;
    private ResultSet selectUserInfo;
    private JSONObject jsonObject;
    private conRs conrs;
    private Connection con;
    private PreparedStatement pstm;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            HttpSession session = request.getSession();
            userId = session.getAttribute("idUser").toString();

            columnName = " * ";
            tableName = " employee_emp_org ";
            whereCondition = " employee_id = '" + userId + "'";

            conrs = SelectQueryDao.selectQueryWithWhereClause(columnName, tableName, whereCondition);
            
            con = conrs.getCon();
            selectUserInfo = conrs.getRs();
            pstm = conrs.getPstm();
            
            while (selectUserInfo.next()) {
                jsonObject = new JSONObject();
                jsonObject.put("empOrgId", selectUserInfo.getString("employee_organogram_id"));
                jsonObject.put("userName", selectUserInfo.getString("user_name"));
                jsonObject.put("password", selectUserInfo.getString("password"));
                jsonObject.put("fullName", selectUserInfo.getString("full_name"));
                jsonObject.put("contactInfo", selectUserInfo.getString("contact_info"));
                jsonObject.put("contactCell", selectUserInfo.getString("contact_cell"));
                jsonObject.put("contactEmail", selectUserInfo.getString("contact_email"));
                jsonObject.put("designation", selectUserInfo.getString("designation"));
                jsonObject.put("department", selectUserInfo.getString("department"));
                jsonObject.put("parentId", selectUserInfo.getString("parent_id"));
            }

            String json = new Gson().toJson(jsonObject);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (SQLException | JSONException ex) {
            Logger.getLogger(UserInfo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pstm.close();
                selectUserInfo.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
