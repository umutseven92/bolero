package com.bolero.game.dtos;

import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class NpcsDTO {
  private List<NpcDTO> npcs;

  public Optional<NpcDTO> getNpcDTOFromName(String name) {
    return npcs.stream().filter(n -> n.getName().equals(name)).findFirst();
  }
}
