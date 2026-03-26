# Hospital Management System

## Complete Class Diagram (UML Format)

```
╔════════════════════════════════════════════════════════════════════════════════╗
║                      HOSPITAL MANAGEMENT SYSTEM                               ║
║                           CLASS DIAGRAM (UML)                                 ║
╚════════════════════════════════════════════════════════════════════════════════╝


┌─────────────────────────────────────────────────┐
│           IHospitalService                      │
│          (Interface)                            │
├─────────────────────────────────────────────────┤
│ METHODS:                                        │
│ + getAppointmentById(int): Appointment          │
│ + getAppointmentsForPatient(int): List          │
│ + getAppointmentsForDoctor(int): List           │
│ + scheduleAppointment(Appointment): boolean     │
│ + updateAppointment(Appointment): boolean       │
│ + cancelAppointment(int): boolean               │
└────────────────────┬────────────────────────────┘
                     │
                     │ implements
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│      HospitalServiceImpl                         │
│    (Concrete Implementation)                    │
├─────────────────────────────────────────────────┤
│ ATTRIBUTES:                                     │
│ (none - all static/method scope)                │
├─────────────────────────────────────────────────┤
│ METHODS:                                        │
│ + getAppointmentById(int): Appointment          │
│ - getAppointmentById(ResultSet): Appointment    │
│ + getAppointmentsForPatient(int): List          │
│ + getAppointmentsForDoctor(int): List           │
│ + scheduleAppointment(Appointment): boolean     │
│ + updateAppointment(Appointment): boolean       │
│ + cancelAppointment(int): boolean               │
│ - executeInsert(Connection, Appointment): bool  │
│ - executeUpdate(Connection, Appointment): bool  │
│ - executeDelete(Connection, int): boolean       │
│ - buildAppointment(ResultSet): Appointment      │
└────────────────────┬────────────────────────────┘
                     │
                     │ uses
                     │
        ┌────────────┴────────────┬──────────────┐
        │                         │              │
        ▼                         ▼              ▼
   ┌─────────────┐         ┌──────────────┐  ┌─────────────┐
   │  Patient    │         │   Doctor     │  │ Appointment │
   │  (Entity)   │         │   (Entity)   │  │   (Entity)  │
   └─────────────┘         └──────────────┘  └─────────────┘


┌─────────────────────────────────────────────────┐
│              Patient                            │
│           (Entity/Model Class)                  │
├─────────────────────────────────────────────────┤
│ ATTRIBUTES:                                     │
│ - patientId: int                                │
│ - patientName: String                           │
│ - patientEmail: String                          │
│ - patientPhoneNumber: String                    │
├─────────────────────────────────────────────────┤
│ CONSTRUCTORS:                                   │
│ + Patient()                                     │
│ + Patient(int, String, String, String)          │
├─────────────────────────────────────────────────┤
│ GETTERS:                                        │
│ + getPatientId(): int                           │
│ + getPatientName(): String                      │
│ + getPatientEmail(): String                     │
│ + getPatientPhoneNumber(): String               │
├─────────────────────────────────────────────────┤
│ SETTERS:                                        │
│ + setPatientId(int): void                       │
│ + setPatientName(String): void                  │
│ + setPatientEmail(String): void                 │
│ + setPatientPhoneNumber(String): void           │
├─────────────────────────────────────────────────┤
│ OTHER METHODS:                                  │
│ + toString(): String                            │
└─────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────┐
│              Doctor                             │
│           (Entity/Model Class)                  │
├─────────────────────────────────────────────────┤
│ ATTRIBUTES:                                     │
│ - doctorId: int                                 │
│ - doctorName: String                            │
│ - specialization: String                        │
├─────────────────────────────────────────────────┤
│ CONSTRUCTORS:                                   │
│ + Doctor()                                      │
│ + Doctor(int, String, String)                   │
├─────────────────────────────────────────────────┤
│ GETTERS:                                        │
│ + getDoctorId(): int                            │
│ + getDoctorName(): String                       │
│ + getSpecialization(): String                   │
├─────────────────────────────────────────────────┤
│ SETTERS:                                        │
│ + setDoctorId(int): void                        │
│ + setDoctorName(String): void                   │
│ + setSpecialization(String): void               │
├─────────────────────────────────────────────────┤
│ OTHER METHODS:                                  │
│ + toString(): String                            │
└─────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│           Appointment                            │
│         (Entity/Model Class)                     │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - appointmentId: int                             │
│ - patientId: int  ────────┐ (Foreign Key)        │
│ - doctorId: int   ────────┤ (Foreign Key)        │
│ - appointmentDate: Date                          │
│ - description: String                            │
├──────────────────────────────────────────────────┤
│ CONSTRUCTORS:                                    │
│ + Appointment()                                  │
│ + Appointment(int, int, int, Date, String)      │
├──────────────────────────────────────────────────┤
│ GETTERS:                                         │
│ + getAppointmentId(): int                        │
│ + getPatientId(): int                            │
│ + getDoctorId(): int                             │
│ + getAppointmentDate(): Date                     │
│ + getDescription(): String                       │
├──────────────────────────────────────────────────┤
│ SETTERS:                                         │
│ + setAppointmentId(int): void                    │
│ + setPatientId(int): void                        │
│ + setDoctorId(int): void                         │
│ + setAppointmentDate(Date): void                 │
│ + setDescription(String): void                   │
├──────────────────────────────────────────────────┤
│ OTHER METHODS:                                   │
│ + toString(): String                             │
└──────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│              DBConnection                        │
│         (Utility/Singleton Class)                │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - connection: Connection (static, private)       │
├──────────────────────────────────────────────────┤
│ CONSTRUCTORS:                                    │
│ - DBConnection() (private - prevent instantiation)
├──────────────────────────────────────────────────┤
│ STATIC METHODS:                                  │
│ + getConnection(): Connection (static)           │
│   └─ Returns single shared connection instance   │
│   └─ Creates only once, reuses after             │
├──────────────────────────────────────────────────┤
│ INTERNAL LOGIC:                                  │
│ 1. Checks if connection is null                  │
│ 2. If null, loads MySQL driver                   │
│ 3. Creates connection to HMS1 database           │
│ 4. Returns connection (reuses every time)        │
└──────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│              PropertyUtil                        │
│         (Utility/Helper Class)                   │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ - property_file: String (static, final, private) │
│   = "util/db.properties"                         │
├──────────────────────────────────────────────────┤
│ CONSTRUCTORS:                                    │
│ - PropertyUtil() (private - prevent instantiation)
├──────────────────────────────────────────────────┤
│ STATIC METHODS:                                  │
│ + getPropertyString(): String (static)           │
│   └─ Reads db.properties file                    │
│   └─ Extracts db.url, db.username, db.password   │
│   └─ Returns formatted connection string         │
│       Example: jdbc:mysql://localhost:3306/HMS1 │
│                 ?user=root&password=pass123      │
└──────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│         PatientNumberNotFoundException           │
│       (Custom Exception Class)                   │
├──────────────────────────────────────────────────┤
│ EXTENDS:                                         │
│ → extends Exception                              │
├──────────────────────────────────────────────────┤
│ CONSTRUCTORS:                                    │
│ + PatientNumberNotFoundException(String message) │
│   └─ Calls super(message) to initialize          │
├──────────────────────────────────────────────────┤
│ USAGE:                                           │
│ Used when:                                       │
│ - Patient ID is invalid                          │
│ - Patient doesn't exist in database              │
│ Thrown by: getAppointmentsForPatient()           │
└──────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│              MainModule                          │
│         (Presentation/UI Class)                  │
├──────────────────────────────────────────────────┤
│ ATTRIBUTES:                                      │
│ (All attributes are local - method scope)        │
├──────────────────────────────────────────────────┤
│ STATIC METHODS:                                  │
│ + main(String[] args): void                      │
│   └─ Entry point of program                      │
│   └─ Infinite loop showing menu                  │
│   └─ Handles user input                          │
├──────────────────────────────────────────────────┤
│ PRIVATE STATIC METHODS (UI handlers):            │
│ - getAppointmentById(service, scanner): void     │
│ - getAppointmentsForPatient(service, scanner)    │
│ - getAppointmentsForDoctor(service, scanner)     │
│ - scheduleAppointment(service, scanner): void    │
│ - updateAppointment(service, scanner): void      │
│ - cancelAppointment(service, scanner): void      │
├──────────────────────────────────────────────────┤
│ FLOW:                                            │
│ 1. main() creates HospitalServiceImpl instance    │
│ 2. Creates Scanner for input                     │
│ 3. while(true) loop                              │
│ 4. Shows menu (6 options + exit)                 │
│ 5. Calls appropriate method based on choice      │
│ 6. Method gets user input via Scanner            │
│ 7. Calls service methods                         │
│ 8. Displays results                              │
│ 9. Returns to menu                               │
└──────────────────────────────────────────────────┘


╔════════════════════════════════════════════════════════════════════════════════╗
║                        RELATIONSHIPS BETWEEN CLASSES                           ║
╚════════════════════════════════════════════════════════════════════════════════╝


DEPENDENCY RELATIONSHIPS:
───────────────────────

MainModule
    │
    ├─ DEPENDS ON → IHospitalService (interface)
    │               └─ Uses methods to perform operations
    │
    ├─ DEPENDS ON → Scanner (from java.util)
    │               └─ Gets user input
    │
    ├─ DEPENDS ON → Appointment (entity)
    │               └─ Creates appointment objects
    │
    └─ DEPENDS ON → SimpleDateFormat (from java.text)
                    └─ Parses date strings


HospitalServiceImpl
    │
    ├─ IMPLEMENTS → IHospitalService
    │               └─ Concrete implementation
    │
    ├─ USES → DBConnection
    │          └─ Gets database connection
    │
    ├─ USES → Appointment (entity)
    │          └─ Works with appointment data
    │
    ├─ USES → Patient (entity)
    │          └─ Validates patient exists
    │
    ├─ USES → Doctor (entity)
    │          └─ Validates doctor exists
    │
    └─ THROWS → PatientNumberNotFoundException
               └─ When patient not found


COMPOSITION RELATIONSHIPS:
──────────────────────

Appointment REFERENCES:
    │
    ├─ patientId (Foreign Key)
    │  └─ References Patient.patientId
    │
    └─ doctorId (Foreign Key)
       └─ References Doctor.doctorId


DATABASE RELATIONSHIPS:
────────────────────

Patient Table ──┐
               │
             ┌─┴─┐
            1    *  (One Patient → Many Appointments)
               │
          Appointment Table
               │
            *  1
             └─┬─┐
               │
            Doctor Table

One Patient ─ Many Appointments
One Doctor  ─ Many Appointments
One Appointment ─ One Patient + One Doctor


╔════════════════════════════════════════════════════════════════════════════════╗
║                         PACKAGE ORGANIZATION                                   ║
╚════════════════════════════════════════════════════════════════════════════════╝


mainmod (package)
    └─ MainModule.java
       └─ main() → Entry point
       └─ Presentation layer


dao (package)
    ├─ IHospitalService.java (interface)
    │  └─ Contract for all service operations
    │
    └─ HospitalServiceImpl.java (implementation)
       └─ Business logic + Database access


entity (package)
    ├─ Patient.java
    │  └─ Patient data model
    │
    ├─ Doctor.java
    │  └─ Doctor data model
    │
    └─ Appointment.java
       └─ Appointment data model


util (package)
    ├─ DBConnection.java
    │  └─ Database connection (Singleton)
    │
    ├─ PropertyUtil.java
    │  └─ Read database properties
    │
    └─ db.properties (file)
       └─ Database credentials


myexceptions (package)
    └─ PatientNumberNotFoundException.java
       └─ Custom exception


╔════════════════════════════════════════════════════════════════════════════════╗
║                      CLASS INTERACTION FLOW                                    ║
╚════════════════════════════════════════════════════════════════════════════════╝


1. USER LAUNCHES PROGRAM
   └─ MainModule.main() executes

2. MAIN METHOD RUNS
   └─ Creates HospitalServiceImpl hospitalService
   └─ Creates Scanner scanner
   └─ Enters while(true) loop

3. DISPLAY MENU
   └─ Shows 6 options
   └─ Waits for user choice

4. USER SELECTS OPTION 4 (Schedule Appointment)
   └─ Calls MainModule.scheduleAppointment()

5. GET USER INPUT
   └─ Scanner gets Patient ID, Doctor ID, Date, Description
   └─ Creates Appointment object

6. CALL SERVICE
   └─ hospitalService.scheduleAppointment(appointment)
   └─ Goes to HospitalServiceImpl.scheduleAppointment()

7. SERVICE VALIDATES
   └─ Checks if Patient exists (validates patientId)
   └─ Checks if Doctor exists (validates doctorId)
   └─ Validates appointment date
   └─ May throw PatientNumberNotFoundException

8. SERVICE CALLS DAO
   └─ Same class but different method
   └─ Calls executeInsert(connection, appointment)

9. GET CONNECTION
   └─ DBConnection.getConnection()
   └─ Returns shared connection instance
   └─ Created only once (Singleton)

10. BUILD SQL QUERY
    └─ "INSERT INTO Appointment (PatientID, DoctorID, AppointmentDate, Description)"
    └─ Uses PreparedStatement with ? placeholders

11. EXECUTE QUERY
    └─ preparedStatement.setInt(1, patientId)
    └─ preparedStatement.setInt(2, doctorId)
    └─ preparedStatement.setDate(3, appointmentDate)
    └─ preparedStatement.setString(4, description)
    └─ preparedStatement.executeUpdate()

12. DATABASE PROCESSES
    └─ MySQL receives query
    └─ Checks if PatientID exists (Foreign Key)
    └─ Checks if DoctorID exists (Foreign Key)
    └─ Inserts new row into Appointment table
    └─ Confirms with rowsInserted count

13. RETURN RESULT
    └─ HospitalServiceImpl returns boolean (true/false)

14. DISPLAY RESULT
    └─ MainModule displays:
    │  "✅ Appointment scheduled successfully!" (if true)
    │  "❌ Failed to schedule appointment" (if false)

15. RETURN TO MENU
    └─ Loop continues
    └─ Shows menu again


╔════════════════════════════════════════════════════════════════════════════════╗
║                      ATTRIBUTE AND METHOD TYPES                                ║
╚════════════════════════════════════════════════════════════════════════════════╝


VISIBILITY MODIFIERS:
─────────────────

+ PUBLIC
  └─ Accessible from anywhere
  └─ Used for: Interface methods, public getters/setters

- PRIVATE
  └─ Accessible only within same class
  └─ Used for: Attributes, internal helper methods
  └─ Ensures encapsulation

~ PACKAGE (no modifier)
  └─ Accessible within same package
  └─ Used for: Some utility methods


STATIC vs INSTANCE:
──────────────────

STATIC (not tied to instance):
  └─ DBConnection.getConnection() - one connection for all
  └─ PropertyUtil.getPropertyString() - utility method
  └─ MainModule.main() - entry point

INSTANCE (tied to object):
  └─ Patient attributes (each patient has their own data)
  └─ Doctor attributes (each doctor has their own data)
  └─ Appointment attributes (each appointment has its own data)


ABSTRACT vs CONCRETE:
────────────────────

ABSTRACT (Interface):
  └─ IHospitalService
  └─ Defines contract but no implementation

CONCRETE (Implementation):
  └─ HospitalServiceImpl
  └─ Provides actual implementation
  └─ Entity classes (Patient, Doctor, Appointment)


╔════════════════════════════════════════════════════════════════════════════════╗
║                    FULL CLASS DIAGRAM LEGEND                                   ║
╚════════════════════════════════════════════════════════════════════════════════╝


Line Types:
──────────

→ Dependency (uses)
  └─ A uses B
  └─ Temporary relationship

▶ Implementation (realizes)
  └─ Class implements Interface
  └─ Must implement all methods

─ Association
  └─ Objects related
  └─ One class uses another class


Relationships Summary:
─────────────────────

HospitalServiceImpl ▶ IHospitalService
  └─ ServiceImpl implements IHospitalService interface

MainModule → HospitalServiceImpl
  └─ MainModule depends on (uses) HospitalServiceImpl

HospitalServiceImpl → Patient, Doctor, Appointment
  └─ Service uses entity classes

HospitalServiceImpl → DBConnection
  └─ Service gets connection from DBConnection

Appointment → Patient (foreign key)
  └─ Appointment references Patient via patientId

Appointment → Doctor (foreign key)
  └─ Appointment references Doctor via doctorId


╔════════════════════════════════════════════════════════════════════════════════╗
║                      SUMMARY: WHAT EACH CLASS DOES                             ║
╚════════════════════════════════════════════════════════════════════════════════╝


Patient (Entity)
  ├─ Holds patient data (ID, name, email, phone)
  ├─ Getters/Setters for attributes
  └─ toString() for display

Doctor (Entity)
  ├─ Holds doctor data (ID, name, specialization)
  ├─ Getters/Setters for attributes
  └─ toString() for display

Appointment (Entity)
  ├─ Holds appointment data (ID, patientId, doctorId, date, description)
  ├─ Getters/Setters for attributes
  ├─ Foreign keys reference Patient and Doctor
  └─ toString() for display

IHospitalService (Interface)
  ├─ Defines contract for all service operations
  ├─ Methods: getAppointmentById, scheduleAppointment, etc.
  └─ Implemented by: HospitalServiceImpl

HospitalServiceImpl (Service)
  ├─ Implements IHospitalService
  ├─ Business logic: validate appointments, process requests
  ├─ Data access: SQL queries via PreparedStatements
  └─ Uses: DBConnection, entity classes

DBConnection (Singleton Utility)
  ├─ Manages database connection
  ├─ Creates only ONE connection instance
  ├─ Reuses same connection for all operations
  └─ Loads MySQL driver, connects to HMS1 database

PropertyUtil (Utility)
  ├─ Reads db.properties file
  ├─ Extracts database credentials
  └─ Builds connection string

PatientNumberNotFoundException (Exception)
  ├─ Custom exception class
  ├─ Thrown when patient not found
  └─ Extends Exception

MainModule (Presentation)
  ├─ Entry point (main method)
  ├─ Shows menu and gets user input
  ├─ Calls service methods
  ├─ Displays results
  └─ Infinite loop until exit
