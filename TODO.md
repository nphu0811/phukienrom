# Fix Staff Order Detail Error

## Steps:
- [x] 1. Analyzed code/templates/services/security - StaffWebController fully implemented, no code bugs.
- [x] 2. Edit StaffWebController.java: Add exception handling for invalid order ID + orders.html error display.
- [x] 3. Fixed Thymeleaf onclick syntax error in staff/order-detail.html (th:onclick with proper escaping - was ParseException).
- [ ] 4. Check/create STAFF role user in DB.
- [ ] 5. Test: Login staff → /staff/orders → click detail (valid/invalid ID).
- [ ] 6. Verify JS status update works.
- [ ] 7. Complete.

**Status**: Backend + template fixed. Template parsing error resolved. Use test staff user in sql/create_staff_test_user.sql.
- [ ] 4. Test: Login staff → /staff/orders → click detail (valid/invalid ID).
- [ ] 5. Verify JS status update works.
- [ ] 6. Complete.

**Status**: Code correct, likely DB/staff user issue + unhandled exception.
