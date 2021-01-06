// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.sps.TimeRange;
import java.util.*;
import java.util.Comparator;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    List<TimeRange> withOptionalAttendees = getTimeRanges(events, request, true);
    List<TimeRange> withoutOptionalAttendees = getTimeRanges(events, request, false);

    if (withOptionalAttendees.size() > 0) {
      return withOptionalAttendees;
    } else {
      return withoutOptionalAttendees;
    }
  }

  /** Return the time range of the available slots for the requested meeting */
  private List<TimeRange> getTimeRanges(Collection<Event> events, MeetingRequest request,
      boolean includeOptional) {
    List<TimeRange> freeTimes = new ArrayList<>();
    int requestedMeetingDuration = (int) request.getDuration();

    if (requestedMeetingDuration > TimeRange.WHOLE_DAY.duration()) {
      return freeTimes;
    }

    // Sort timeranges of events in ascending order by start time
    List<TimeRange> busyTimes = getBusyTimes(events, request, includeOptional);
    Collections.sort(busyTimes, TimeRange.ORDER_BY_START);

    // No events
    if (busyTimes.size() == 0) {
      freeTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
      return freeTimes;
    }

    // One event
    if (busyTimes.size() == 1) {
      int eventStart = busyTimes.get(0).start();
      int eventEnd = busyTimes.get(0).end();
        
        if (eventStart - requestedMeetingDuration > TimeRange.START_OF_DAY) {
          freeTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, eventStart, false));
        }
        if (eventEnd + requestedMeetingDuration < TimeRange.END_OF_DAY) {
          freeTimes.add(TimeRange.fromStartEnd(busyTimes.get(0).end(), TimeRange.END_OF_DAY,
              true));
        } 
        return freeTimes;
    }

    List<TimeRange> mergedTimes = getMergedTimes(busyTimes);

    // Add durations before the first event if any
    if (mergedTimes.get(0).start() - requestedMeetingDuration > TimeRange.START_OF_DAY) {
      freeTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busyTimes.get(0).start(),
          false));
    }
    
    for (int i = 0; i < mergedTimes.size() - 1; i++) {
      int gapStartTime = mergedTimes.get(i).end();
      int gapEndTime = mergedTimes.get(i + 1).start();
      int gap = gapEndTime - gapStartTime;

      if (gap < requestedMeetingDuration) {
        continue;
      }

      TimeRange freeTime = TimeRange.fromStartEnd(gapStartTime, gapEndTime, false);
      freeTimes.add(freeTime);
    }
    // Add durations after the last event if any
    if (mergedTimes.get(mergedTimes.size() - 1).end() + requestedMeetingDuration < 
        TimeRange.END_OF_DAY) {
      freeTimes.add(TimeRange.fromStartEnd(mergedTimes.get(mergedTimes.size() - 1).end(),
          TimeRange.END_OF_DAY, true));
    }
    return freeTimes;
  }

  /** Returns the time ranges of events of the meeting's attendee */
  private List<TimeRange> getBusyTimes(Collection<Event> events, MeetingRequest request, 
      boolean includeOptional) {
    List<TimeRange> busyTimes = new ArrayList<>();

    if (includeOptional) {
        // Mandatory and optional meeting attendees
        Set<String> allAttendees = new HashSet<>();
        allAttendees.addAll(request.getAttendees());
        allAttendees.addAll(request.getOptionalAttendees());
        for (Event event : events) {
          if (containsAny(event.getAttendees(), allAttendees)) {
            busyTimes.add(event.getWhen());
          }
        }
    } else {
        for (Event event : events) {
          if (containsAny(event.getAttendees(), new HashSet<String>(request.getAttendees()))) {
            busyTimes.add(event.getWhen());
          }
        }
    }
    return busyTimes;
  }

  /** Merges overlapping time ranges and returns a list of the merged times */
  private List<TimeRange> getMergedTimes(List<TimeRange> busyTimes) {
    List<TimeRange> mergedTimes = new ArrayList<>();
    int index = 0;
    mergedTimes.add(busyTimes.get(0));

    for (int i = 1; i < busyTimes.size(); i++) {
      TimeRange thisRange = mergedTimes.get(index);
      TimeRange otherRange = busyTimes.get(i);
          
      if (thisRange.overlaps(otherRange) || thisRange.contains(otherRange)) {
        TimeRange mergedTime = TimeRange.fromStartEnd(
            Math.min(thisRange.start(), otherRange.start()),
            Math.max(thisRange.end(), otherRange.end()), false);
        mergedTimes.set(index, mergedTime);
      } else {
        index++;
        mergedTimes.add(busyTimes.get(i));
      }
    }
    return mergedTimes;
  }

  /** Return true if setA contains any of the elements in setB */
  private boolean containsAny(Set<String> setA, Set<String> setB) {
    for (String element : setB) {
      if (setA.contains(element)) {
        return true;
      }
    }
    return false;
  }
}
