import java.io.*;
import java.sql.*;
import java.text.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.Formatter;
import java.util.regex.*;
import oracle.jdbc.OracleTypes;
import java.util.logging.*;

public class EMSystem {
	static Connection con;
	static BufferedReader br ;
	static Formatter fmt; 
	private final static Logger logr = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
	
	public static void main(String[] args) {
		
		br = new BufferedReader(new InputStreamReader(System.in));
		setupLogger() ;
		Base64.Encoder encoder = Base64.getEncoder();
		
		do {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");

				System.out.println("\n==================================================================== EMS - Employee Management System ====================================================================");
				System.out.println("\n1. Login in EMS.");
				System.out.println("2. Exit System.");
				System.out.print("\nEnter Choice> ");
				String ch = br.readLine();

				switch(ch) {
				case "1":
					System.out.print("\nEnter User: ");
					String user=br.readLine();
					System.out.print("Enter Password: ");
					String pass =br.readLine();
					try {
						PreparedStatement pst =con.prepareStatement("select * from login_master where role=? and password=?");
						pst.setString(1, user);
						pst.setString(2, pass);

						ResultSet rs =pst.executeQuery();

						if(pass.isEmpty() || user.isEmpty()) {
							System.out.println("\nUser Role and Password required.");
						}
						else {
							if(rs.next()) {
								String encrypt=rs.getString("password");
								String encrypt_pass=encoder.encodeToString(encrypt.getBytes());

								String user_type=rs.getString("role");
								if(user_type.equals("ADMIN")) {
									do {
										System.out.println("\n+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
										System.out.println("|==================================================================== EMS - Employee Management System ====================================================================|");
										System.out.println("+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
										System.out.println("\nLogged in as "+rs.getString("role"));
										System.out.println("Encrypted Password: "+encrypt_pass);
										System.out.println("\n 1. Add Employee.");
										System.out.println(" 2. Update Employee.");
										System.out.println(" 3. Delete Employee.");
										System.out.println(" 4. Search Employee.");
										System.out.println(" 5. Add Department.");
										System.out.println(" 6. View Department.");
										System.out.println(" 7. Create RL.");
										System.out.println(" 8. View RL.");
										System.out.println(" 9. Status report.");
										System.out.println("10. Logout.");
										System.out.print("\nEnter Choice> ");
										String admin_choice=br.readLine();

										switch(admin_choice) {
										case "1":addEmployee(); 
										break;
										case "2":editEmployee();
										break;
										case "3":deleteEmployee();
										break;
										case "4":searchEmployee();
										break;
										case "5":addDepartment();
										break;
										case "6":searchDepartment();
										break;
										case "7":createRL();
										break;
										case "8":viewRL();
										break;
										case "9":status();
										break;
										case "10":
											break;
										default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
										}
										if(admin_choice.equals("10")) {
											System.out.println("\nSession Logout.");
											break;
										}
									}while(true);
								}
								else {
									do {
										System.out.println("\n+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
										System.out.println("|==================================================================== EMS - Employee Management System ====================================================================|");
										System.out.println("+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
										System.out.println("\nLogged in as "+rs.getString(3));
										System.out.println("Encrypted Password: "+encrypt_pass);
										int myid=rs.getInt(1);
										pst =con.prepareStatement("select * from employees where empid=?");
										fmt= new Formatter();
										pst.setInt(1, myid);
										pst.executeQuery();
										rs =pst.executeQuery();
										System.out.println("\nYour Details:");
										System.out.println("+-------------+----------------------------+-----------------------------+---------------+-------------------------------------+-------------------+");
										System.out.format("|   %-10s|		%-16s   |	      %-16s   | %-12s |		  %-21s|  %-16s |\n", "EMP ID","FIRST NAME","LAST NAME","DATE OF BIRTH","EMAIL","DEPARTMENT NAME");
										System.out.println("+-------------+----------------------------+-----------------------------+---------------+-------------------------------------+-------------------+");
										if(rs.next()) {

											System.out.format("|%-13d|%-28s|%-29s|%-15s|%-37s|%-19s|\n", rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),rs.getString(5),rs.getInt(6));
										}
										System.out.println("+-------------+----------------------------+-----------------------------+---------------+-------------------------------------+-------------------+");

										System.out.println("\n\n1. View Department. ");
										System.out.println("2. Your Credentials.");
										System.out.println("3. Edit Password.");
										System.out.println("4. Create RL. ");
										System.out.println("5. View RL.");
										System.out.println("6. Status Report.");
										System.out.println("7. Logout.");
										System.out.print("\nEnter Choice> ");
										String user_choice=br.readLine();
										switch(user_choice) {
										case "1":searchDepartment();
										break;
										case "2":viewCredentials(myid);
										break;
										case "3":userPassword();
										break;
										case "4":createRL();
										break;
										case "5":viewRLUser();
										break;
										case "6":statusUser(myid);
										break;
										case "7":
											break;
										default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
										}
										if(user_choice.equals("7")) {
											System.out.println("\nSession Logout.");
											break;
										}
									}while(true);
								}
							}
							else {
								System.out.println("\nWrong Credentials.");
							}
						}
					}catch(Exception e) {


					}
					finally {
						con.close();
					}
					break;
				case "2":System.out.println("\nExited Successfully. Thank You For Using EMS-Employee Management System.");
				System.exit(0);
				break;
				default:System.out.println("\nInvalid Input Please Select Correct Choice.");
				}

			}catch(Exception e) {
				System.out.println("\nInvalid Input Please Enter Number Value.");
				logr.log(Level.FINE,"Exception Here: ",e);
				System.out.println();
				System.out.println();
			}
		}while(true);
	}

	static public void addEmployee() {
		try {
			//PROCEDURE addEmp_sp(?,?,?,?,?)
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			CallableStatement pst =con.prepareCall("{call addEmp_sp(?,?,?,?,?)}");

			do {
				System.out.println("\n+=======================+");
				System.out.println("| Add Employee Details. |");
				System.out.println("+=======================+");
				System.out.println("\n1. Add Employee.");
				System.out.println("2. Go Back.");
				System.out.print("\nEnter Choice> ");
				String emp=br.readLine();
				switch(emp) {
				case "1":
					System.out.println("\n+--------------------------+");
					System.out.println("| Adding Employee Details. |");
					System.out.println("+--------------------------+");
					System.out.print("\nEnter First Name: ");
					String fname=br.readLine();
					if(fname.isEmpty()) {
						throw new RequiredField();
					}
					System.out.print("Enter Last Name: ");
					String lname=br.readLine();
					if(lname.isEmpty()) {
						throw new RequiredField();
					}
					System.out.print("Date Of Birth (DD/MM/YYYY): ");
					String dob=br.readLine();
					if(dob.matches("^$")) {
						throw new RequiredField();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date= sdf.parse(dob);
					long ms= date.getTime();
					java.sql.Date sdob= new java.sql.Date(ms);

					String sday=sdob.toString();
					String day=sday.substring(8);
					int iday=Integer.parseInt(day);

					String smonth=sdob.toString();
					String month=smonth.substring(5, 7);
					int imonth=Integer.parseInt(month);

					String syear=sdob.toString();
					String year=syear.substring(0,4);
					int iyear=Integer.parseInt(year);

					LocalDate ld =LocalDate.now();
					LocalDate ldob=LocalDate.of(iyear,imonth, iday);
					Period period =Period.between(ldob, ld);
					if(period.getYears()<=24) {
						throw new InvalidAge();
					}

					System.out.print("Email: ");
					String email=br.readLine();
					if(email.matches("^$")) {
						throw new RequiredField();
					}
					String expression="^(.+)@(.+)$";
					Pattern pattern=Pattern.compile(expression);
					Matcher match= pattern.matcher(email);

					if(!match.matches()) {
						throw new InvalidEmailFormat();
					}

					System.out.print("Department ID: ");
					String sdeptid=br.readLine();
					if(sdeptid.matches("^$")) {
						throw new RequiredField();
					}
					int deptid=Integer.parseInt(sdeptid);


					pst.setString(1,fname);
					pst.setString(2,lname);
					pst.setDate(3,sdob);
					pst.setString(4,email);
					pst.setInt(5,deptid);
					pst.executeUpdate();
					System.out.println("\nSuccessfully Added User.");
					break;
				case "2":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");	
				}
				if(emp.equals("2")) {
					break;
				}
			}while(true);


		}catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(InvalidAge e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(InvalidEmailFormat e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(ParseException e) {
			System.out.println("\nWrong Date Format Please Enter Valid Date.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}

	}

	static public void editEmployee() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			Base64.Encoder encoder = Base64.getEncoder();
			PreparedStatement pst;
			ResultSet rs;

			do {
				System.out.println("\n+==========================+");
				System.out.println("| Update Employee Details. |");
				System.out.println("+==========================+");
				System.out.println("\n1. Update First Name. ");
				System.out.println("2. Update Last Name. ");
				System.out.println("3. Update Date Of Birth. ");
				System.out.println("4. Update Email. ");
				System.out.println("5. Update Department ID. ");
				System.out.println("6. Update User Role. ");
				System.out.println("7. Update Password. ");
				System.out.println("8. Go Back.");
				System.out.print("\nEnter Choice> ");

				String update =br.readLine();
				switch(update) {
				case "1":
					pst =con.prepareStatement("update employees set firstname=?  where empid=?");
					System.out.println("\n+---------------------------+");
					System.out.println("| Edit Employee First Name. |");
					System.out.println("+---------------------------+");
					System.out.print("\nEnter Employee ID: ");
					String sempid=br.readLine();
					if(sempid.matches("^$")) {
						throw new RequiredField();
					}
					int empid=Integer.parseInt(sempid);

					System.out.print("Edit First Name: ");
					String fname=br.readLine();
					if(fname.isEmpty()) {
						throw new RequiredField();
					}
					pst.setString(1, fname);
					pst.setInt(2, empid);
					pst.executeUpdate();
					rs =pst.executeQuery();
					if(rs.next()) {
						System.out.println("\nSuccessfully Updated First Name.");	
					}else{
						System.out.println("\nInvalid Input First Name Not Updated.");	
					}

					break;
				case "2":
					pst =con.prepareStatement("update employees set lastname=?  where empid=?");
					System.out.println("\n+--------------------------+");
					System.out.println("| Edit Employee Last Name. |");
					System.out.println("+--------------------------+");
					System.out.print("\nEnter Employee ID: ");
					String sempid1=br.readLine();
					if(sempid1.matches("^$")) {
						throw new RequiredField();
					}
					int empid1=Integer.parseInt(sempid1);

					System.out.print("Edit Last Name: ");
					String lname=br.readLine();
					if(lname.isEmpty()) {
						throw new RequiredField();
					}
					pst.setString(1, lname);
					pst.setInt(2, empid1);
					pst.executeUpdate();
					pst.executeUpdate();
					rs =pst.executeQuery();
					if(rs.next()) {
						System.out.println("\nSuccessfully Updated Last Name.");	
					}else{
						System.out.println("\nInvalid Input Last Name Not Updated.");	
					}

					break;
				case "3":
					pst =con.prepareStatement("update employees set dob=?  where empid=?");
					System.out.println("\n+--------------------+");
					System.out.println("| Edit Employee DOB. |");
					System.out.println("+--------------------+");
					System.out.print("\nEnter Employee ID: ");
					String sempid2=br.readLine();
					if(sempid2.matches("^$")) {
						throw new RequiredField();
					}
					int empid2=Integer.parseInt(sempid2);

					System.out.print("Edit Date Of Birth (DD/MM/YYYY): ");
					String dob=br.readLine();
					if(dob.isEmpty()) {
						throw new RequiredField();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date= sdf.parse(dob);
					long ms= date.getTime();
					java.sql.Date sdob= new java.sql.Date(ms);

					pst.setDate(1, sdob);
					pst.setInt(2, empid2);
					pst.executeUpdate();
					rs =pst.executeQuery();
					if(rs.next()) {
						System.out.println("\nSuccessfully Updated Date Of Birth.");	
					}else{
						System.out.println("\nInvalid Input Date Of Birth Not Updated.");	
					}
					break;
				case "4":
					pst =con.prepareStatement("update employees set email=?  where empid=?");
					System.out.println("\n+----------------------+");
					System.out.println("| Edit Employee Email. |");
					System.out.println("+----------------------+");
					System.out.print("\nEnter Employee ID: ");
					String sempid3=br.readLine();
					if(sempid3.matches("^$")) {
						throw new RequiredField();
					}
					int empid3=Integer.parseInt(sempid3);

					System.out.print("Edit Email : ");
					String email=br.readLine();
					String expression="^(.+)@(.+)$";
					Pattern pattern=Pattern.compile(expression);
					Matcher match= pattern.matcher(email);
					if(email.isEmpty()) {
						throw new RequiredField();
					}
					if(!match.matches()) {
						throw new InvalidEmailFormat();
					}
					pst.setString(1, email);
					pst.setInt(2, empid3);
					pst.executeUpdate();
					rs =pst.executeQuery();
					if(rs.next()) {
						System.out.println("\nSuccessfully Updated Email.");	
					}else{
						System.out.println("\nEmail Not Updated/ Invalid Input.");	
					}
					break;
				case "5":
					pst =con.prepareStatement("update employees set department_id=?  where empid=?");
					System.out.println("\n+------------------------------+");
					System.out.println("| Edit Employee Department ID. |");
					System.out.println("+------------------------------+");
					System.out.print("\nEnter Employee ID: ");
					String sempid4=br.readLine();
					if(sempid4.matches("^$")) {
						throw new RequiredField();
					}
					int empid4=Integer.parseInt(sempid4);

					System.out.print("Edit Department ID: ");
					String sdeptid=br.readLine();
					int deptid=Integer.parseInt(sdeptid);
					if(sdeptid.isEmpty()) {
						throw new RequiredField();
					}
					pst.setInt(1, deptid);
					pst.setInt(2, empid4);
					pst.executeUpdate();
					rs =pst.executeQuery();
					if(rs.next()) {
						System.out.println("\nSuccessfully Updated Department ID.");	
					}else{
						System.out.println("\nInvalid Input Department ID Not Updated.");	
					}
					break;
				case "6":
					pst =con.prepareStatement("update login_master set role=?  where userid=?");
					System.out.println("\n+-----------------+");
					System.out.println("| Edit User Role. |");
					System.out.println("+-----------------+");
					System.out.print("\nEnter User ID: ");
					String sempid5=br.readLine();
					if(sempid5.matches("^$")) {
						throw new RequiredField();
					}
					int empid5=Integer.parseInt(sempid5);

					System.out.print("Edit User Role (ADMIN/ USER): ");
					String role=br.readLine();
					role.toUpperCase();
					if(role.isEmpty()) {
						throw new RequiredField();
					}
					pst.setString(1, role.toUpperCase());
					pst.setInt(2, empid5);
					pst.executeUpdate();
					rs =pst.executeQuery();
					if(rs.next()) {

						System.out.println("\nSuccessfully Updated User Role.");	

					}else{
						System.out.println("\nInvalid Input User Role Not Updated.");	
					}
					break;
				case "7":
					pst=con.prepareStatement("update login_master set password=? where userid=?");
					System.out.println("\n+---------------------+");
					System.out.println("| Edit User Password. |");
					System.out.println("+---------------------+");
					System.out.print("\nEnter User ID: ");
					String suid=br.readLine();
					if(suid.matches("^$")) {
						throw new RequiredField();
					}
					int uid=Integer.parseInt(suid);

					System.out.print("Enter User Password: ");
					String pass = br.readLine();


					pst.setString(1, pass);
					pst.setInt(2, uid);
					rs=pst.executeQuery();


					pst.executeUpdate();
					rs=pst.executeQuery();
					if(rs.next()) {
						System.out.println("\nSuccessfully Updated User Password.");
					}
					else {
						System.out.println("\nInvalid Input User Password Not Updated.");
					}
					break;
				case "8":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");	

				}
				if(update.equals("8")) {
					break;
				}
			}while(true);
		}
		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(ParseException e) {
			System.out.println("\nWrong Date Format Please Enter Valid Date.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(InvalidEmailFormat e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}

	}

	static public void deleteEmployee() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			PreparedStatement pst,pst1;
			ResultSet rs;

			do {
				System.out.println("\n+==========================+");
				System.out.println("| Employee Delete Section. |");
				System.out.println("+==========================+");
				System.out.println("\n1. Delete Employee.");
				System.out.println("2. Go Back.");
				System.out.print("\nEnter Choice> ");
				String del =br.readLine();
				switch(del) {
				case "1":
					pst=con.prepareStatement("delete from login_master where userid=?");
					pst1=con.prepareStatement("delete from employees where empid=?");
					System.out.println("\n+--------------------+");
					System.out.println("| Deleting Employee. |");
					System.out.println("+--------------------+");
					System.out.print("\nEnter Employee ID To Be Deleted: ");
					String sempid=br.readLine();
					if(sempid.matches("^$")) {
						throw new RequiredField();
					}
					int empid=Integer.parseInt(sempid);

					pst.setInt(1, empid);
					pst1.setInt(1, empid);
					int n=pst.executeUpdate();
					n=pst1.executeUpdate();

					if(n==0) {
						System.out.println("\nEmployee Not Deleted | Input Not Found.");
					}
					else {
						System.out.println("\nEmployee Deleted Successfully.");
					}

					break;
				case "2":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(del.equals("2")) {
					break;
				}

			}while(true);
		}
		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value."+e);
			logr.log(Level.FINE,"Exception Here: ",e);
			e.printStackTrace();
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value."+e);
			logr.log(Level.FINE,"Exception Here: ",e);
			e.printStackTrace();
			System.out.println();
			System.out.println();
		}
	}

	static public void searchEmployee() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			CallableStatement cst;
			Base64.Encoder encoder = Base64.getEncoder();
			fmt= new Formatter();
			PreparedStatement pst;
			ResultSet rs;

			do {
				System.out.println("\n+=========================+");
				System.out.println("| View Employees Details. |");
				System.out.println("+=========================+");
				System.out.println("\n1. Search Individual Employee.");
				System.out.println("2. Display Employees List.");
				System.out.println("3. Display All Employees Credentials.");
				System.out.println("4. View Employee Credentials.");
				System.out.println("5. Go Back.");
				System.out.print("\nEnter Choice> ");
				String search=br.readLine();
				switch(search) {
				case "1":
					pst =con.prepareStatement("select * from employees where empid=?");
					System.out.println("\n+------------------------+");
					System.out.println("| View Employee Details. |");
					System.out.println("+------------------------+");
					System.out.print("\nEnter Employee ID: ");
					String sempid=br.readLine();
					if(sempid.matches("^$")) {
						throw new RequiredField();
					}
					int empid=Integer.parseInt(sempid);

					pst.setInt(1, empid);
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nEmployee Details:");
					System.out.println("+-------------+----------------------------+-----------------------------+---------------+-------------------------------------+-------------------+");
					System.out.format("|   %-10s|		%-16s   |	      %-16s   | %-12s |		  %-21s|  %-16s |\n", "EMP ID","FIRST NAME","LAST NAME","DATE OF BIRTH","EMAIL","DEPARTMENT NAME");
					System.out.println("+-------------+----------------------------+-----------------------------+---------------+-------------------------------------+-------------------+");
					if(rs.next()) {

						System.out.format("|%-13d|%-28s|%-29s|%-15s|%-37s|%-19s|\n", rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),rs.getString(5),rs.getInt(6));
					}
					else {
						System.out.format("|             %78s 					                   |\n","[Data Not Found | Enter Correct Emp ID]");
					}
					System.out.println("+-------------+----------------------------+-----------------------------+---------------+-------------------------------------+-------------------+");

					break;
				case "2"://PROCEDURE getAllEmp_sp
					cst =con.prepareCall("{call getAllEmp_sp(?)}");
					cst.registerOutParameter(1, OracleTypes.CURSOR);
					rs=cst.executeQuery();
					rs=(ResultSet) cst.getObject(1);
					System.out.println("\nAll Employees Detail List:");
					System.out.println("+-----------+---------------------+-----------------------+---------------------+----------------------------------------+-----------------+");
					System.out.format("|%10s|	  %-16s|	  %-16s| %16s	|		%-25s|%16s |\n", "EMPLOYEE ID","FIRST NAME","LAST NAME","DATE OF BIRTH","EMAIL","DEPARTMENT NAME");
					System.out.println("+-----------+---------------------+-----------------------+---------------------+----------------------------------------+-----------------+");
					while(rs.next()){
						System.out.format("|%-11s|%-21s|%-23s|%-21s|%-40s|%-17s|\n", rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),rs.getString(5),rs.getString(6));
					}
					System.out.println("+-----------+---------------------+-----------------------+---------------------+----------------------------------------+-----------------+");

					break;
				case "3":
					pst =con.prepareStatement("select * from login_master");
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nAll Employees Credential Details:");
					System.out.println("+-------------+-------------------------------------------+---------------+");
					System.out.format("|   %-10s|		   %-31s|     %-10s|\n", "USER ID","ENCRYPTED PASSWORD","ROLE");
					System.out.println("+-------------+-------------------------------------------+---------------+");
					while(rs.next()) {
						String encrypt=rs.getString("password");
						String encrypt_pass=encoder.encodeToString(encrypt.getBytes());
						System.out.format("|%-13d| %-31s		  |%-15s|\n", rs.getInt(1),encrypt_pass,rs.getString(3));
					}
					System.out.println("+-------------+-------------------------------------------+---------------+");
					break;
				case "4"://PROCEDURE getUserDetails_sp(?,?,?)
					cst =con.prepareCall("{call getUserDetails_sp(?,?,?)}");
					System.out.println("\n+------------------------+");
					System.out.println("| View User Credentials. |");
					System.out.println("+------------------------+");
					System.out.print("\nEnter User ID: ");
					String suid=br.readLine();
					if(suid.matches("^$")) {
						throw new RequiredField();
					}
					int uid=Integer.parseInt(suid);

					cst.setInt(1, uid);
					cst.registerOutParameter(2, Types.VARCHAR);
					cst.registerOutParameter(3, Types.VARCHAR);
					cst.executeQuery();
					rs=cst.executeQuery();

					System.out.println("\nEmployee Credential Details:");
					System.out.println("+-------------+-------------------------------------------+---------------+");
					System.out.format("|   %-10s|		   %-31s|     %-10s|\n", "USER ID","ENCRYPTED PASSWORD","ROLE");
					System.out.println("+-------------+-------------------------------------------+---------------+");
					String encrypt=cst.getString(2);
					String encrypt_pass=encoder.encodeToString(encrypt.getBytes());
					System.out.format("|%-13d| %-31s		  |%-15s|\n", cst.getInt(1),encrypt_pass,cst.getString(3));
					System.out.println("+-------------+-------------------------------------------+---------------+");

					break;
				case "5":
					break;
				default:System.out.println("Invalid Input Please Select The Correct Choice.");
				}
				if(search.equals("5")) {
					break;
				}
			}while(true);
		}
		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NullPointerException e) {
			System.out.format("|                %-55s  |\n","[Data Not Found | Enter Correct User ID]");
			System.out.println("+-------------+-------------------------------------------+---------------+");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void addDepartment() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			PreparedStatement pst;
			ResultSet rs;
			do {
				System.out.println("\n+=========================+");
				System.out.println("| Add Department Details. |");
				System.out.println("+=========================+");
				System.out.println("\n1. Add New Department.");
				System.out.println("2. Delete Department.");
				System.out.println("3. Go Back.");
				System.out.print("\nEnter Choice> ");
				String dept=br.readLine();
				switch(dept) {
				case "1":
					pst=con.prepareStatement("insert into department (department_id, department_nm) values (department_seq.NEXTVAL,?)");
					System.out.println("\n+--------------------+");
					System.out.println("| Adding Department. |");
					System.out.println("+--------------------+");
					System.out.print("\nEnter Department Name: ");
					String deptname=br.readLine();
					if(deptname.isEmpty()) {
						throw new RequiredField();
					}
					pst.setString(1, deptname);
					pst.executeUpdate();
					System.out.println("\nSuccessfully Added New Department.");
					break;
				case "2":
					pst=con.prepareStatement("delete from department where department_id=?");
					System.out.println("\n+----------------------+");
					System.out.println("| Deleting Department. |");
					System.out.println("+----------------------+");
					System.out.print("\nEnter Department ID To Be Deleted: ");
					String sempid=br.readLine();
					if(sempid.matches("^$")) {
						throw new RequiredField();
					}
					int empid=Integer.parseInt(sempid);

					pst.setInt(1, empid);
					int n=pst.executeUpdate();
					if(n==0) {
						System.out.println("\nEmployee Not Deleted | Input Not Found.");
					}
					else {
						System.out.println("\nEmployee Deleted Successfully.");
					}

					break;
				case "3":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(dept.equals("3")) {
					break;
				}
			}while(true);

		}
		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void searchDepartment() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			fmt= new Formatter();
			PreparedStatement pst;
			ResultSet rs;

			do {
				System.out.println("\n+==================+");
				System.out.println("| View Department. |");
				System.out.println("+==================+");
				System.out.println("\n1. Search Department.");
				System.out.println("2. Go Back.");
				System.out.print("\nEnter Choice> ");
				String dept=br.readLine();
				switch(dept) {
				case "1":
					pst =con.prepareStatement("select department_id, department_nm from department");
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nDepartment List: ");
					System.out.println("+---------------+-----------------+");
					System.out.format("| %-10s | %-10s |\n", "DEPARTMENT ID","DEPARTMENT NAME");
					System.out.println("+---------------+-----------------+");
					while(rs.next()) {
						System.out.format("| %-13d | %-15s |\n", rs.getInt(1),rs.getString(2));
					}
					System.out.println("+---------------+-----------------+");


					break;
				case "2":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(dept.equals("2")) {
					break;
				}
			}while(true);

		}
		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void createRL() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			PreparedStatement pst;
			ResultSet rs;
			do {
				System.out.println("\n+======================================+");
				System.out.println("| Add Regulation/ Legislation Details. |");
				System.out.println("+======================================+");
				System.out.println("\n1. Create Regulation/ Legislation.");
				System.out.println("2. Go Back.");
				System.out.print("\nEnter Choice> ");
				String rl=br.readLine();
				switch(rl) {
				case "1":
					pst=con.prepareStatement("insert into compliance (complianceid,rlType,details,createDate,department_id) values (compliance_seq.NEXTVAL,?,?,?,?)");
					System.out.println("\n+----------------------+");
					System.out.println("| Creating RL Details. |");
					System.out.println("+----------------------+");
					System.out.print("\nEnter RL Type: ");
					String rltype=br.readLine();
					if(rltype.isEmpty()) {
						throw new RequiredField();
					}
					System.out.print("Enter Details: ");
					String details =br.readLine();
					if(details.isEmpty()) {
						throw new RequiredField();
					}
					System.out.print("Enter Date (DD/MM/YYYY): ");
					String daterl=br.readLine();
					if(daterl.matches("^$")) {
						throw new RequiredField();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date= sdf.parse(daterl);
					long ms= date.getTime();
					java.sql.Date sdrl= new java.sql.Date(ms);

					System.out.print("Enter Department ID: ");
					String sdeptid=br.readLine();
					if(sdeptid.matches("^$")) {
						throw new RequiredField();
					}
					int deptid=Integer.parseInt(sdeptid);
					pst.setString(1, rltype);
					pst.setString(2, details);
					pst.setDate(3, sdrl);
					pst.setInt(4, deptid);
					pst.executeUpdate();
					System.out.println("\nSuccessfully Created RL Compliance.");
					break;
				case "2":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(rl.equals("2")) {
					break;
				}
			}while(true);
		}catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(ParseException e) {
			System.out.println("\nWrong Date Format Please Enter Valid Date.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(InvalidEmailFormat e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void viewRL() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			fmt= new Formatter();
			PreparedStatement pst;
			ResultSet rs;
			do {
				System.out.println("\n+=======================================+");
				System.out.println("| View Regulation/ Legislation Details. |");
				System.out.println("+=======================================+");
				System.out.println("\n1. View Regulation/ Legislation.");
				System.out.println("2. RL Compliance Tracking.");
				System.out.println("3. Go Back.");
				System.out.print("\nEnter Choice> ");
				String rl=br.readLine();
				switch(rl) {
				case "1":
					pst=con.prepareStatement("select * from compliance");
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nView Regulation/ Legislation: ");
					System.out.println("+---------------+-----------------------+--------------------------------------------------------------------------------------------+-------------------+-----------------+");
					System.out.format("| %-10s |        %-15s| 					  %-50s |    %-15s|  %-15s|\n", "COMPLIANCE ID","RL TYPE","DETAILS","CREATE DATE","DEPARTMENT ID");
					System.out.println("+---------------+-----------------------+--------------------------------------------------------------------------------------------+-------------------+-----------------+");
					while(rs.next()) {
						System.out.format("|%-15d|%-23s| %-90s |%-19s|%-17s|\n", rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),rs.getInt(5));
					}
					System.out.println("+---------------+-----------------------+--------------------------------------------------------------------------------------------+-------------------+-----------------+");

					break;
				case "2":
					pst=con.prepareStatement("select com.complianceid as complianceid, com.department_id, dept.department_nm,rlType,details,com.createDate, count(distinct emp.empid) AS empcount ,count(distinct sts.empid) as statuscount FROM department dept,employees emp,compliance com LEFT OUTER JOIN statusreport sts ON  sts.department_id = com.department_id AND sts.complianceid = com.complianceid WHERE com.department_id = emp.department_id AND com.department_id = dept.department_id GROUP BY com.complianceid,com.department_id,dept.department_nm,rlType,details,com.createDate");
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nCompliance Tracking: ");
					System.out.println("+---------------+---------------+-----------------+------------------------------+---------------------------------------------------------------------------------------+-------------+-----------+---------------------+");
					System.out.format("| %-10s | %-10s | %-10s | 	      %-18s |                                          %-45s| %-10s | %-10s| %-10s |\n", "COMPLIANCE ID","DEPARTMENT ID","DEPARTMENT NAME","RL TYPE","DETAILS","CREATE DATE","TOTAL EMP","TOTAL STATUS REPORT");
					System.out.println("+---------------+---------------+-----------------+------------------------------+---------------------------------------------------------------------------------------+-------------+-----------+---------------------+");
					while(rs.next()) {
						System.out.format("|%-15s|%-15s|%-17s|%-30s|%-59s     			 |%-13s|%-11s|%-21s|\n", rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getDate(6),rs.getInt(7),rs.getInt(8));
					}
					System.out.println("+---------------+---------------+-----------------+------------------------------+---------------------------------------------------------------------------------------+-------------+-----------+---------------------+");
					break;
				case "3":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(rl.equals("3")) {
					break;
				}
			}while(true);
		}catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(InvalidEmailFormat e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void status() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			PreparedStatement pst;
			ResultSet rs;
			fmt= new Formatter();
			do {
				System.out.println("\n+========================+");
				System.out.println("| Status Report Details. |");
				System.out.println("+========================+");
				System.out.println("\n1. Status Report.");
				System.out.println("2. Create Report.");
				System.out.println("3. Go Back.");
				System.out.print("\nEnter Choice> ");
				String rl=br.readLine();
				switch(rl) {
				case "1":
					pst=con.prepareStatement("select * from statusreport");
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nStatus Report:");
					System.out.println("+---------------+-----------+--------------+-------------+-----------------+---------------+");
					System.out.format("| %-10s | %-10s|    %-10s|   %-10s|  %-15s| %10s |\n", "COMPLIANCE ID","STATUS ID","EMP ID","COMMENT","CREATE DATE","DEPARTMENT ID");
					System.out.println("+---------------+-----------+--------------+-------------+-----------------+---------------+");
					while(rs.next()){
						System.out.format("|%-15d|%-11d|%-14s|%-13s|%-17s|%-15s|\n", rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getDate(5),rs.getInt(6));
					}
					System.out.println("+---------------+-----------+--------------+-------------+-----------------+---------------+");

					break;
				case "2":
					pst=con.prepareStatement("insert into statusreport (complianceid,statusrptid ,empid,comments,createDate, department_id) values (?,statusreport_seq.NEXTVAL,?,?,?,?)");
					System.out.println("\n+------------------------------+");
					System.out.println("| Enter Status Report Details. |");
					System.out.println("+------------------------------+");
					System.out.print("\nEnter Compliance ID: ");
					String scid=br.readLine();
					if(scid.matches("^$")) {
						throw new RequiredField();
					}
					int cid=Integer.parseInt(scid);

					System.out.print("Enter Employee ID: ");
					String sempid =br.readLine();
					if(sempid.matches("^$")) {
						throw new RequiredField();
					}
					int empid=Integer.parseInt(sempid);

					System.out.print("Enter Comment: ");
					String comment=br.readLine();
					if(comment.isEmpty()) {
						throw new RequiredField();
					}
					System.out.print("Enter Date (DD/MM/YYYY): ");
					String daterl=br.readLine();
					if(daterl.matches("^$")) {
						throw new RequiredField();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date= sdf.parse(daterl);
					long ms= date.getTime();
					java.sql.Date sdrl= new java.sql.Date(ms);

					System.out.print("Enter Department ID: ");
					String sdeptid=br.readLine();
					if(sdeptid.matches("^$")) {
						throw new RequiredField();
					}
					int deptid=Integer.parseInt(sdeptid);


					pst.setInt(1, cid);
					pst.setInt(2, empid);
					pst.setString(3, comment);
					pst.setDate(4,sdrl);
					pst.setInt(5, deptid);
					pst.executeUpdate();
					System.out.println("\nStatus Reported.");
					break;
				case "3":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(rl.equals("3")) {
					break;
				}
			}while(true);
		}catch(SQLIntegrityConstraintViolationException e) {
			System.out.println("\nWrong ID's Have Been Entered.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(ParseException e) {
			System.out.println("\nWrong Date Format Please Enter Valid Date.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(InvalidEmailFormat e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void statusUser(int i) {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			PreparedStatement pst;
			ResultSet rs;
			fmt= new Formatter();
			do {
				System.out.println("\n+========================+");
				System.out.println("| Status Report Details. |");
				System.out.println("+========================+");
				System.out.println("\n1. Status Report.");
				System.out.println("2. Create Report.");
				System.out.println("3. Go Back.");
				System.out.print("\nEnter Choice> ");
				String rl=br.readLine();
				switch(rl) {
				case "1":
					pst=con.prepareStatement("select * from statusreport where empid=?");
					pst.setInt(1, i);
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nYour Status Report:");
					System.out.println("+---------------+-----------+--------------+-------------+-----------------+---------------+");
					System.out.format("| %-10s | %-10s|    %-10s|   %-10s|  %-15s| %10s |\n", "COMPLIANCE ID","STATUS ID","EMP ID","COMMENT","CREATE DATE","DEPARTMENT ID");
					System.out.println("+---------------+-----------+--------------+-------------+-----------------+---------------+");
					if(rs.next()) {
						System.out.format("|%-15d|%-11d|%-14s|%-13s|%-17s|%-15s|\n", rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getDate(5),rs.getInt(6));
					}
					else {
						System.out.format("|                       %-40s                   |\n", "[No Status Available/ Submit Your Status Report]");
					}
					System.out.println("+---------------+-----------+--------------+-------------+-----------------+---------------+");

					break;
				case "2":
					pst=con.prepareStatement("insert into statusreport (complianceid,statusrptid ,empid,comments,createDate, department_id) values (?,statusreport_seq.NEXTVAL,?,?,?,?)");
					System.out.println("\n+---------------------------+");
					System.out.println("| Submit Your Status Report. |");
					System.out.println("+---------------------------+");
					System.out.println("\nSubmit Your Status Report");
					System.out.print("\nEnter Compliance ID: ");
					String scid=br.readLine();
					if(scid.matches("^$")) {
						throw new RequiredField();
					}
					int cid=Integer.parseInt(scid);

					System.out.print("Enter Employee ID: ");
					String sempid =br.readLine();
					if(sempid.matches("^$")) {
						throw new RequiredField();
					}
					int empid=Integer.parseInt(sempid);

					System.out.print("Enter Comment: ");
					String comment=br.readLine();
					if(comment.isEmpty()) {
						throw new RequiredField();
					}
					System.out.print("Enter Date (DD/MM/YYYY): ");
					String daterl=br.readLine();
					if(daterl.matches("^$")) {
						throw new RequiredField();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date= sdf.parse(daterl);
					long ms= date.getTime();
					java.sql.Date sdrl= new java.sql.Date(ms);

					System.out.print("Enter Department ID: ");
					String sdeptid=br.readLine();
					if(sdeptid.matches("^$")) {
						throw new RequiredField();
					}
					int deptid=Integer.parseInt(sdeptid);


					pst.setInt(1, cid);
					pst.setInt(2, empid);
					pst.setString(3, comment);
					pst.setDate(4,sdrl);
					pst.setInt(5, deptid);
					pst.executeUpdate();
					System.out.println("\nStatus Reported.");
					break;
				case "3":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(rl.equals("3")) {
					break;
				}
			}while(true);
		}catch(SQLIntegrityConstraintViolationException e) {
			System.out.println("\nWrong ID's Have Been Entered.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(ParseException e) {
			System.out.println("\nWrong Date Format Please Enter Valid Date.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(InvalidEmailFormat e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void viewRLUser() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			fmt= new Formatter();
			PreparedStatement pst;
			ResultSet rs;
			do {
				System.out.println("\n+=============================+");
				System.out.println("| View RL Compliance Details. |");
				System.out.println("+=============================+");
				System.out.println("\n1. View Regulation/ Legislation.");
				System.out.println("2. Go Back.");
				System.out.print("\nEnter Choice> ");
				String rl=br.readLine();
				switch(rl) {
				case "1":
					pst=con.prepareStatement("select * from compliance");
					pst.executeQuery();
					rs =pst.executeQuery();
					System.out.println("\nView Regulation/ Legislation: ");
					System.out.println("+---------------+-----------------------+--------------------------------------------------------------------------------------------+-------------------+-----------------+");
					System.out.format("| %-10s |        %-15s| 					  %-50s |    %-15s|  %-15s|\n", "COMPLIANCE ID","RL TYPE","DETAILS","CREATE DATE","DEPARTMENT ID");
					System.out.println("+---------------+-----------------------+--------------------------------------------------------------------------------------------+-------------------+-----------------+");
					while(rs.next()) {
						System.out.format("|%-15d|%-23s| %-90s |%-19s|%-17s|\n", rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4),rs.getInt(5));
					}
					System.out.println("+---------------+-----------------------+--------------------------------------------------------------------------------------------+-------------------+-----------------+");

					break;
				case "2":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(rl.equals("2")) {
					break;
				}
			}while(true);
		}catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(InvalidEmailFormat e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	//User Credentials
	static public void viewCredentials(int i) {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			Base64.Encoder encoder = Base64.getEncoder();
			fmt= new Formatter();
			CallableStatement cst;
			PreparedStatement pst;
			ResultSet rs;
			do {
				System.out.println("\n+===================+");
				System.out.println("| Your Credentials. |");
				System.out.println("+===================+");
				System.out.println("\n1. View Credentials.");
				System.out.println("2. Go Back.");
				System.out.print("\nEnter Choice> ");
				String view=br.readLine();
				switch(view) {
				case "1":
					cst =con.prepareCall("{call getUserDetails_sp(?,?,?)}");
					cst.setInt(1, i);
					cst.registerOutParameter(2, Types.VARCHAR);
					cst.registerOutParameter(3, Types.VARCHAR);
					cst.executeQuery();
					rs=cst.executeQuery();

					System.out.println("\nEmployee Credential Details:");
					System.out.println("+-------------+-------------------------------------------+---------------+");
					System.out.format("|   %-10s|		   %-31s|     %-10s|\n", "USER ID","ENCRYPTED PASSWORD","ROLE");
					System.out.println("+-------------+-------------------------------------------+---------------+");
					String encrypt=cst.getString(2);
					String encrypt_pass=encoder.encodeToString(encrypt.getBytes());
					System.out.format("|%-13d| %-31s		  |%-15s|\n", cst.getInt(1),encrypt_pass,cst.getString(3));
					System.out.println("+-------------+-------------------------------------------+---------------+");
					break;
				case "2":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");					
				}
				if(view.equals("2")) {
					break;
				}
			}while(true);
		}catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NullPointerException e) {
			System.out.format("|                %-55s  |\n","[Data Not Found | Enter Correct User ID]");
			System.out.println("+-------------+-------------------------------------------+---------------+");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}

	static public void userPassword() {
		try {
			setupLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
			Base64.Encoder encoder = Base64.getEncoder();
			fmt= new Formatter();
			CallableStatement cst;
			PreparedStatement pst;
			ResultSet rs;
			do {
				System.out.println("\n+==================+");
				System.out.println("| Change Password. |");
				System.out.println("+==================+");
				System.out.println("\n1. Edit Your Password.");
				System.out.println("2. Go Back.");
				System.out.print("\nEnter Choice> ");
				String pass=br.readLine();
				switch(pass) {
				case "1":
					pst=con.prepareStatement("update login_master set password=? where userid=?");
					System.out.println("\n+---------------------+");
					System.out.println("| Edit Your Password. |");
					System.out.println("+---------------------+");
					System.out.print("\nEnter Your User ID: ");
					String suid=br.readLine();
					if(suid.matches("^$")) {
						throw new RequiredField();
					}
					int uid=Integer.parseInt(suid);
					System.out.print("Enter Your User Password: ");
					String pass1 = br.readLine();
					pst.setString(1, pass1);
					pst.setInt(2, uid);
					rs=pst.executeQuery();
					pst.executeUpdate();
					rs=pst.executeQuery();
					if(rs.next()) {
						System.out.println("\nSuccessfully Updated User Password.");
					}
					else {
						System.out.println("\nInvalid Input User Password Not Updated.");
					}
					break;
				case "2":
					break;
				default:System.out.println("\nInvalid Input Please Select The Correct Choice.");
				}
				if(pass.equals("2")) {
					break;
				}
			}while(true);
		}		catch(SQLException e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NullPointerException e) {
			
			System.out.println("\nFields Cannot be Empty.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
		catch(NumberFormatException e) {
			System.out.println("\nInvalid Input Please Enter Number Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(RequiredField e) {
			System.out.println(e.getMessage());
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}catch(Exception e) {
			System.out.println("\nInvalid Input Please Enter Correct Value.");
			logr.log(Level.FINE,"Exception Here: ",e);
			System.out.println();
			System.out.println();
		}
	}


	private static void setupLogger() {
		LogManager.getLogManager().reset();
		logr.setLevel(Level.ALL);

		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.SEVERE);
		logr.addHandler(ch);

		try {
			FileHandler fh = new FileHandler("log.txt", true);
			fh.setLevel(Level.FINE);
			logr.addHandler(fh);
			SimpleFormatter sf = new SimpleFormatter();
			fh.setFormatter(sf);
		} catch (Exception e) {            
			logr.log(Level.FINE, "File logger not working.", e);
		}
	}
}
