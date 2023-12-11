package com.api.backend.notification.data.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

  private final Map<Long, Map<String, SseEmitter>> teamEmitters = new ConcurrentHashMap<>();
  private final Map<Long, Object> eventCache = new ConcurrentHashMap<>();

  public void save(Long teamId, String emitterId, SseEmitter sseEmitter) {
    if (!teamEmitters.containsKey(teamId)) {
      Map<String, SseEmitter> teamParticipantMap = new ConcurrentHashMap<>();
      teamParticipantMap.put(emitterId, sseEmitter);

      teamEmitters.put(teamId, teamParticipantMap);
    } else {
      Map<String, SseEmitter> teamParticipantMap = teamEmitters.get(teamId);
      teamParticipantMap.put(emitterId, sseEmitter);

      teamEmitters.put(teamId, teamParticipantMap);
    }
  }



  /**
   * emitter를 지움
   */
  public void deleteById(Long teamId, String emitterId) {
    if (!teamEmitters.containsKey(teamId)) {
      return;
    }

    Map<String, SseEmitter> teamParticipantMap = teamEmitters.get(teamId);
    teamParticipantMap.remove(emitterId);

    if (teamParticipantMap.isEmpty()) {
      teamEmitters.remove(teamId);
      return;
    }

    teamEmitters.put(teamId, teamParticipantMap);
  }

  public List<SseEmitter> getAllByTeamIdAndExcludeEmitterId(Long teamId, String excludeEmitterId) {
    Map<String, SseEmitter> teamParticipantsEmitters = teamEmitters.get(teamId);
    List<SseEmitter> emitters = new ArrayList<>();

    for (Map.Entry<String, SseEmitter> info : teamParticipantsEmitters.entrySet()) {
      if (!info.getKey().equals(excludeEmitterId)) {
        emitters.add(info.getValue());
      }
    }

    return emitters;
  }
}
