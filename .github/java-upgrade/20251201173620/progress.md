# Upgrade Progress

  ### ✅ Generate Upgrade Plan [View Log](logs\1.generatePlan.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ✅ Install JDK 17
  </details>

  ### ✅ Confirm Upgrade Plan [View Log](logs\2.confirmPlan.log)

  ### ✅ Setup Development Environment [View Log](logs\3.setupEnvironment.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ✅ Install JDK 21
  </details>

  ### ✅ PreCheck [View Log](logs\4.precheck.log)
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ✅ Precheck - Build project [View Log](logs\4.1.precheck-buildProject.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvnw clean test-compile -q -B -fn`
    </details>
  
    ### ✅ Precheck - Validate CVEs [View Log](logs\4.2.precheck-validateCves.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### CVE issues
    </details>
  
    ### ✅ Precheck - Run tests [View Log](logs\4.3.precheck-runTests.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Test result
    | Total | Passed | Failed | Skipped | Errors |
    |-------|--------|--------|---------|--------|
    | 0 | 0 | 0 | 0 | 0 |
    </details>
  </details>

  ### ✅ Upgrade project to use `Java 21`
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ✅ Upgrade using OpenRewrite [View Log](logs\5.1.upgradeProjectUsingOpenRewrite.log)
    1 file changed, 1 insertion(+), 1 deletion(-)
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Recipes
    - [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/UpgradeToJava21)
    </details>
  
    ### ✅ Upgrade using Agent [View Log](logs\5.2.upgradeProjectUsingAgent.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Code changes
    - Upgrade Java version from 17 to 21
    - Apply OpenRewrite recipe UpgradeToJava21
    - Prepare for build validation and fix any compilation issues
    </details>
  
    ### ✅ Build Project [View Log](logs\5.3.buildProject.log)
    Build result: 100% Java files compiled
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvnw clean test-compile -q -B -fn`
    </details>
  </details>

  ### ✅ Validate & Fix
  
  <details>
      <summary>[ click to toggle details ]</summary>
  
  - ###
    ### ❗ Validate CVEs [View Log](logs\6.1.validateCves.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Checked Dependencies
      - org.springframework.boot:spring-boot-starter-parent:4.0.0
      - org.projectlombok:lombok:latest
      - org.postgresql:postgresql:latest
    
    #### CVE issues
    - Dependency `org.postgresql:postgresql` has **3** known CVEs need to be fixed:
      - [CVE-2012-1618](https://github.com/advisories/GHSA-h86w-m5rm-xr33): Unescaped parameters in the PostgreSQL JDBC driver
        - **Severity**: **HIGH**
        - **Details**: Interaction error in the PostgreSQL JDBC driver before 8.2, when used with a PostgreSQL server with the "standard_conforming_strings" option enabled, such as the default configuration of PostgreSQL 9.1, does not properly escape unspecified JDBC statement parameters, which allows remote attackers to perform SQL injection attacks.  NOTE: as of 20120330, it was claimed that the upstream developer planned to dispute this issue, but an official dispute has not been posted as of 20121005.
      - [CVE-2022-31197](https://github.com/advisories/GHSA-r38f-c4h4-hqq2): PostgreSQL JDBC Driver SQL Injection in ResultSet.refreshRow() with malicious column names
        - **Severity**: **HIGH**
        - **Details**: ### Impact
          _What kind of vulnerability is it? Who is impacted?_
          
          The PGJDBC implementation of the `java.sql.ResultRow.refreshRow()` method is not performing escaping of column names so a malicious column name that contains a statement terminator, e.g. `;`, could lead to SQL injection. This could lead to executing additional SQL commands as the application's JDBC user.
          
          User applications that do not invoke the `ResultSet.refreshRow()` method are not impacted.
          
          User application that do invoke that method are impacted if the underlying database that they are querying via their JDBC application may be under the control of an attacker. The attack requires the attacker to trick the user into executing SQL against a table name who's column names would contain the malicious SQL and subsequently invoke the `refreshRow()` method on the ResultSet.
          
          For example:
          
          ```sql
          CREATE TABLE refresh_row_example (
            id     int PRIMARY KEY,
            "1 FROM refresh_row_example; SELECT pg_sleep(10); SELECT * " int
          );
          ```
          
          This example has a table with two columns. The name of the second column is crafted to contain a statement terminator followed by additional SQL. Invoking the `ResultSet.refreshRow()` on a ResultSet that queried this table, e.g. `SELECT * FROM refresh_row`, would cause the additional SQL commands such as the `SELECT pg_sleep(10)` invocation to be executed.
          
          As the multi statement command would contain multiple results, it would not be possible for the attacker to get data directly out of this approach as the `ResultSet.refreshRow()` method would throw an exception. However, the attacker could execute any arbitrary SQL including inserting the data into another table that could then be read or any other DML / DDL statement.
          
          Note that the application's JDBC user and the schema owner need not be the same. A JDBC application that executes as a privileged user querying database schemas owned by potentially malicious less-privileged users would be vulnerable. In that situation it may be possible for the malicious user to craft a schema that causes the application to execute commands as the privileged user.
          
          ### Patches
          _Has the problem been patched? What versions should users upgrade to?_
          
          Yes, versions 42.2.26, 42.3.7, and 42.4.1 have been released with a fix.
          
          ### Workarounds
          _Is there a way for users to fix or remediate the vulnerability without upgrading?_
          
          Check that you are not using the `ResultSet.refreshRow()` method.
          
          If you are, ensure that the code that executes that method does not connect to a database that is controlled by an unauthenticated or malicious user. If your application only connects to its own database with a fixed schema with no DDL permissions, then you will not be affected by this vulnerability as it requires a maliciously crafted schema.
      - [CVE-2024-1597](https://github.com/advisories/GHSA-24rp-q3w6-vc56): org.postgresql:postgresql vulnerable to SQL Injection via line comment generation
        - **Severity**: **CRITICAL**
        - **Details**: # Impact
          SQL injection is possible when using the non-default connection property `preferQueryMode=simple` in combination with application code that has a vulnerable SQL that negates a parameter value.
          
          There is no vulnerability in the driver when using the default query mode. Users that do not override the query mode are not impacted.
          
          # Exploitation
          
          To exploit this behavior the following conditions must be met:
          
          1. A placeholder for a numeric value must be immediately preceded by a minus (i.e. `-`)
          1. There must be a second placeholder for a string value after the first placeholder on the same line. 
          1. Both parameters must be user controlled.
          
          The prior behavior of the driver when operating in simple query mode would inline the negative value of the first parameter and cause the resulting line to be treated as a `--` SQL comment. That would extend to the beginning of the next parameter and cause the quoting of that parameter to be consumed by the comment line. If that string parameter includes a newline, the resulting text would appear unescaped in the resulting SQL.
          
          When operating in the default extended query mode this would not be an issue as the parameter values are sent separately to the server. Only in simple query mode the parameter values are inlined into the executed SQL causing this issue.
          
          # Example
          
          ```java
          PreparedStatement stmt = conn.prepareStatement("SELECT -?, ?");
          stmt.setInt(1, -1);
          stmt.setString(2, "\nWHERE false --");
          ResultSet rs = stmt.executeQuery();
          ```
          
          The resulting SQL when operating in simple query mode would be:
          
          ```sql
          SELECT --1,'
          WHERE false --'
          ```
          
          The contents of the second parameter get injected into the command. Note how both the number of result columns and the WHERE clause of the command have changed. A more elaborate example could execute arbitrary other SQL commands.
          
          # Patch
          Problem will be patched upgrade to 42.7.2, 42.6.1, 42.5.5, 42.4.4, 42.3.9, 42.2.28, 42.2.28.jre7
          
          The patch fixes the inlining of parameters by forcing them all to be serialized as wrapped literals. The SQL in the prior example would be transformed into:
          
          ```sql
          SELECT -('-1'::int4), ('
          WHERE false --')
          ```
          
          # Workarounds
          Do not use the connection property`preferQueryMode=simple`. (*NOTE: If you do not explicitly specify a query mode then you are using the default of `extended` and are not impacted by this issue.*)
    </details>
  
    ### ✅ Fix CVE Issues [View Log](logs\6.2.fixCveIssues.log)
    1 file changed, 21 insertions(+), 14 deletions(-)
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Code changes
    - Update <java.version> to 21
    - Add explicit version 42.7.2 for org.postgresql:postgresql
    - Set maven-compiler-plugin <release> to 21 for compile target validation
    </details>
  
    ### ✅ Build Project [View Log](logs\6.3.buildProject.log)
    Build result: 100% Java files compiled
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Command
    `mvnw clean test-compile -q -B -fn`
    </details>
  
    ### ✅ Validate CVEs [View Log](logs\6.4.validateCves.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Checked Dependencies
      - org.postgresql:postgresql:42.7.2
      - org.springframework.boot:spring-boot-starter-parent:4.0.0
    </details>
  
    ### ✅ Validate Code Behavior Changes [View Log](logs\6.5.validateBehaviorChanges.log)
  
    ### ✅ Run Tests [View Log](logs\6.6.runTests.log)
    
    <details>
        <summary>[ click to toggle details ]</summary>
    
    #### Test result
    | Total | Passed | Failed | Skipped | Errors |
    |-------|--------|--------|---------|--------|
    | 0 | 0 | 0 | 0 | 0 |
    </details>
  </details>

  ### ✅ Summarize Upgrade [View Log](logs\7.summarizeUpgrade.log)