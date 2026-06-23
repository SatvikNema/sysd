package org.satvik;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IO.println(String.format("Hello and welcome!"));

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            IO.println("i = " + i);
        }
    }
}

/*

Make a Google calendar
A user can create meetings
Nature of meeting can be one time or recurring
A meeting/event has list of participants, date, description
Participants can take actions on the event - Accept/decline, propose time change, reason
User has a calendar which shows list of events/meetings - day/week/month/year


Entity
User - id, name
Event - id, title, organizer, description, startTime, endTime, recurrence, List<EventParticipant>, status (ACTIVE, CANCELLED)
EventParticipant - User, RSVPStatus, comment, TimeProposal

model
InvitationResponse - eventId, userId, RSVPStatus, TimeProposal, comment
Recurrence - frequency, interval, until, count, days
TimeProposal - startTime, endTime
RSVPStatus - PENDING, ACCEPT, DECLINE, TENTATIVE

Service
EventService -> createEvent, updateEvent(), cancelEvent(), rsvpEvent(), timeProposal()
CalendarService -> getEvent(user, fromDate, toDate), getDayView(), getWeekView(), getMonthView()
NotificationService -> sendInvitation(), sendReminder(), notifyChanges()
 */
