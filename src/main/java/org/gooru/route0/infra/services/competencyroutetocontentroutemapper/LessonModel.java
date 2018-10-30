package org.gooru.route0.infra.services.competencyroutetocontentroutemapper;

import java.util.UUID;

/**
 * @author ashish.
 */
public class LessonModel {

  private final UUID id;
  private final String title;
  private final int sequence;

  public LessonModel(UUID id, String title, int sequence) {
    this.id = id;
    this.title = title;
    this.sequence = sequence;
  }

  public UUID getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public int getSequence() {
    return sequence;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LessonModel that = (LessonModel) o;

    if (sequence != that.sequence) {
      return false;
    }
    if (!id.equals(that.id)) {
      return false;
    }
    return title.equals(that.title);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + title.hashCode();
    result = 31 * result + sequence;
    return result;
  }
}
