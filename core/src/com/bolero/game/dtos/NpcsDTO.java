package com.bolero.game.dtos;

import java.util.List;
import java.util.Optional;

public class NpcsDTO {
  private List<NpcDTO> npcs;

  public List<NpcDTO> getNpcs() {
    return npcs;
  }

  public void setNpcs(List<NpcDTO> npcs) {
    this.npcs = npcs;
  }

  public Optional<NpcDTO> getNpcDTOFromName(String name) {
    return npcs.stream().filter(n -> n.getName().equals(name)).findFirst();
  }
}
