package plat.service.test.impl;

import core.service.impl.SuperMngImpl;
import core.sysconst.DataSourceConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import plat.entity.test.ETest;
import plat.service.test.TestService;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/10 11:45
 */
@Service
public class TestServiceImpl extends SuperMngImpl<ETest> implements TestService {

    @Autowired
    public TestServiceImpl(
            @Qualifier(DataSourceConst.JDBCTEMPLATEWRITE) JdbcTemplate write_jdbcTemplate,
            @Qualifier(DataSourceConst.JDBCTEMPLATEREAD) JdbcTemplate read_jdbcTemplate) {
        super(write_jdbcTemplate, read_jdbcTemplate);
    }
}
