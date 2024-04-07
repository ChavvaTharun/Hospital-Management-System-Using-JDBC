import HospitalManagementSystem.Doctor;
import HospitalManagementSystem.patients;

import java.sql.*;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

        // database url
    static String url ="jdbc:mysql://localhost:3306/Hospital";

        // Database Credentials
    static String username ="root";
    static String password ="tharun123";

        // Establish the connection

    public static void main(String[] args) {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection( url ,username ,password);

            System.out.println("Connected to the database");
            patients patient = new patients(connection,scanner);
            Doctor doctor = new Doctor(connection,scanner);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patients");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your Choice : ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        // Add Patients
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patients
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        // View Doctors
                        doctor.viewDoctor();
                        System.out.println();
                        break;
                    case 4:
                        //Book Appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Enter valid choice !!");
                        break;
                }
            }
        }catch (SQLException e){
            System.out.println("Connection Failed :" +e.getMessage());
        }
    }

      public  static  void  bookAppointment( patients patients , Doctor doctor ,Connection connection,Scanner scanner){
          System.out.println("Enter Patient Id : ");
          int patientId = scanner.nextInt();
          System.out.println("Enter Doctor Id : ");
          int doctorId = scanner.nextInt();
          System.out.println("Enter Appointment date (YYYY-MM-DD) : ");
          String appointmentDate = scanner.next();
          if(patients .getPatientById(patientId) && doctor.getDoctorById(doctorId)){
                 if(checkDoctorAvailability (doctorId , appointmentDate, connection)){
                      String appointmentQuery = "INSERT INTO appointments(patient_id , doctor_id, appointment_date) VALUES(?,?,?)";
                      try{
                          PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                          preparedStatement.setInt(1,patientId);
                          preparedStatement.setInt(2,doctorId);
                          preparedStatement.setString(3,appointmentDate);
                          int rowsAffected = preparedStatement.executeUpdate();
                          if(rowsAffected>0){
                              System.out.println("Appointment Booked !");
                          }else {
                              System.out.println("Failed to book Appointment !!");
                          }
                      }catch (SQLException e){
                          e.printStackTrace();
                      }
                 }else {
                     System.out.println("Doctor Not Available On This Date");
                 }
          }else{
              System.out.println("Either Doctor (or) Patient Doesn't Exist !!!");
          }
      }

      public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if (count ==0){
                    return true;
                }else {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}