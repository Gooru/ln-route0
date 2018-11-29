package org.gooru.route0.infra.data.competency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ashish.
 */
class CompetencyMapImpl implements CompetencyMap {

  private final List<DomainCode> domains;
  private final Map<DomainCode, List<Competency>> domainCodeCompetencyListMap;
  private CompetencyLine ceilingLine, floorLine;

  CompetencyMapImpl(List<Competency> competencies) {
    domainCodeCompetencyListMap = new HashMap<>(competencies.size());

    for (Competency competency : competencies) {
      List<Competency> competenciesForDomain = domainCodeCompetencyListMap
          .get(competency.getDomain());
      if (competenciesForDomain == null) {
        competenciesForDomain = new ArrayList<>();
        competenciesForDomain.add(competency);
        domainCodeCompetencyListMap.put(competency.getDomain(), competenciesForDomain);
      } else {
        competenciesForDomain.add(competency);
      }
    }
    domains = Collections.unmodifiableList(new ArrayList<>(domainCodeCompetencyListMap.keySet()));
    processDomainCompetencyMap();
  }

  private void processDomainCompetencyMap() {
    for (DomainCode domainCode : domains) {
      List<Competency> competencies = domainCodeCompetencyListMap.get(domainCode);
      Set<Competency> competencySet = new HashSet<>(competencies);
      competencies = new ArrayList<>(competencySet);
      competencies.sort(new CompetencySorterByProgression());
      domainCodeCompetencyListMap.put(domainCode, competencies);
    }
  }

  @Override
  public List<DomainCode> getDomains() {
    return domains;
  }

  @Override
  public List<Competency> getCompetenciesForDomain(DomainCode domainCode) {
    return Collections.unmodifiableList(domainCodeCompetencyListMap.get(domainCode));
  }

  @Override
  public CompetencyLine getCeilingLine() {
    if (ceilingLine == null) {
      ceilingLine = CompetencyLine.build(this, true);
    }
    return ceilingLine;
  }

  @Override
  public CompetencyLine getFloorLine() {
    if (floorLine == null) {
      floorLine = CompetencyLine.build(this, false);
    }
    return floorLine;
  }

  @Override
  public String toString() {
    return "CompetencyMap{" + "domains=" + domains + ", domainCodeCompetencyListMap="
        + domainCodeCompetencyListMap
        + ", ceilingLine=" + ceilingLine + ", floorLine=" + floorLine + '}';
  }
}
