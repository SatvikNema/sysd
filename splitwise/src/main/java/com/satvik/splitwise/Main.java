package com.satvik.splitwise;

public class Main {
    public static void main(String[] args) {
        Driver driver = new Driver();
        driver.start();
    }
}
/*

I am working on my LLD skills to prepare for a senior software position. The requirements are as follow

 - The system should allow users to create accounts and manage their profile information.
 - Users should be able to create groups and add other users to the groups.
 - Users should be able to add expenses within a group, specifying the amount, description, and participants.
 - The system should automatically split the expenses among the participants based on their share.
 - Users should be able to view their individual balances with other users and settle up the balances.
 - The system should support different split methods, such as equal split, percentage split, and exact amounts.
 - Users should be able to view their transaction history and group expenses.
 - The system should handle concurrent transactions and ensure data consistency.

 I will have 90 minutes to complete the code, so review accordingly.
 I want you to review the code in a way that it is easy for me to understand and implement within the given time frame.
 Please provide feedback on the overall design, class structure, and any potential improvements or optimizations that can be made.
 Additionally, please suggest any design patterns or best practices that can be applied to enhance the maintainability and scalability of the system.

====== My Solution ======

entities:
 - User -> name
 - ExpenseShare -> User, amount
 - Expense -> id, groupId, amount, description, paidBy, List<ExpenseShare>
 - Group -> id, name, List<Users>, createdBy, List<Expense>

 service
 - UserService -> createUser(name)
 - GroupService -> createGroup(User, List<User>, name), addUser(User, groupId)
 - ExpenseService -> addExpense(SplitInformation, SplitStrategy), updateExpense(Expense)


SplitInformation: user, group, desc, amount, List of users, Map of user share amount, Map of user share %

 */
