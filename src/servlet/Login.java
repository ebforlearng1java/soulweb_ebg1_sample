package servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cmn.DBConnection;

public class Login extends HttpServlet {

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// 画面入力内容の取得
		String userName = request.getParameter("USERNAME");
		String password = request.getParameter("PASSWORD");
		String eBtn = request.getParameter("EBTN"); // イベントボタン

		System.out.println(userName + ":" + password);

		// イベントボタンが"2"の場合、新規登録画面へ遷移
		if ("2".equals(eBtn)) {
			response.sendRedirect("/register.jsp");

			// イベントボタンが"1"の場合、ログイン処理を実施する
		} else {

			// ユーザ有無フラグを取得
			String userId = "";
			try {

				userId = loginLogic(userName, password);

			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			// ユーザ有無の判断
			if (!"".equals(userId)) {

				// sessionへユーザＩＤを設定する
				// TODO
				HttpSession session = request.getSession(true);
				session.setAttribute("userid", userId);

				// 既に登録済みの場合、FIND画面へ遷移
				response.sendRedirect("/find.jsp");
			} else {
				// エラーメッセージを設定
				request.setAttribute("errormsg", "ユーザ存在しません");
				// 登録されない場合、自画面へ遷移
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}
		}

	}

	public String loginLogic(String user, String pw) throws SQLException, ClassNotFoundException {

		String rtn = "";
		String sql = "SELECT * FROM soul_login_t "
				+ "where soul_login_mail = '" + user + "'"
				+ " AND soul_pw = '" + pw + "'";

		// soul_login_t ヘ検索
		ResultSet resultSet = DBConnection.getDBConnection().executeQuery(sql);

		if (resultSet.next()) {
			rtn = resultSet.getString("soul_userid");
		} else {
			// ユーザ存在しない場合、ブランクを返す
			rtn = "";
		}

		resultSet.close();
		DBConnection.dbConClose();

		// 戻り値 ブランク以外 登録済みのユーザＩＤを返す
		//        ブランク     ユーザ存在しない場合、ブランクを返す
		return rtn;

	}

}
