package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dao.SectionDao;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Sections findByLine(Line line) {
        List<Section> sections = sectionDao.findAllByLineId(line.getId());
        return new Sections(sections);
    }

    @Transactional
    public Section add(Section section) {
        return sectionDao.save(section);
    }

    @Transactional
    public void updateSectionsInLine(Line line) {
        deleteByLineId(line.getId());
        addSections(line.getSections());
    }

    private void deleteByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    private void addSections(Sections sections) {
        sectionDao.batchInsert(sections.getSections());
    }
}

