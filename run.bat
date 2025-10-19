@echo off
echo ============================
echo   Compiling BiblioFlow App
echo ============================

:: create output folder
if not exist out mkdir out

:: remove old class files
del /q out\*.class >nul 2>&1

:: Compile all sources with libraries in lib/
javac -cp "lib/*" -d out ^
    DBUtil.java ^
    BarcodeDB.java ^
    User.java ^
    Book.java ^
    Bill.java ^
    SubstoreOrder.java ^
    SubstorePaymentOpt.java ^
    UserDAO.java ^
    BookDAO.java ^
    BillDAO.java ^
    OrderDAO.java ^
    BillGenerator.java ^
    SearchPanel.java ^
    PaymentOpt.java ^
    WrapLayout.java ^
    BookstoreLogin.java ^
    MainApplication.java ^
    frontpage.java ^
    BookstoreUI.java ^
    ScanBarcodePanel.java ^
    DarkLightSwitch.java ^
    SubstoreCartPanel.java ^
    OrderStatusPanel.java ^
    BillHistoryPanel.java ^
    TwilioConfig.java ^
    TwilioSMS.java ^
    BillItem.java ^
    TopSellingBooksApp.java ^
    ExportReportsApp.java ^
    SalesReportApp.java ^
    ProfitLossApp.java ^
    StoreReports.java ^
    Transaction.java ^
    TransactionHistoryPanel.java ^
    RegularCustomerDiscount.java ^
    ManualBillEntryPanel.java ^
    CustomerLoyaltyPanel.java ^
    CustomerLoyaltyService.java ^
    LoyaltyProgramUI.java ^
    LoyaltyAndDiscountUI.java


if errorlevel 1 (
    echo.
    echo Compilation failed!
    pause
    exit /b
)

echo ============================
echo   Running BiblioFlow App
echo ============================

java -cp "lib/*;out" frontpage

echo.
pause
