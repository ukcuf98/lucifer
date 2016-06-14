package plat.service.impl;

import core.service.impl.SuperMngImpl;
import core.sysconst.DataSourceConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import plat.entity.sys.ESysMenu1;
import plat.service.SysMenu1Service;

/**
 * @Description:
 * @author: Lucifer
 * @date: 2016/3/9 16:35
 */
@Service
public class SysMenu1ServiceImpl extends SuperMngImpl<ESysMenu1> implements SysMenu1Service {

    @Autowired
    public SysMenu1ServiceImpl(
            @Qualifier(DataSourceConst.JDBCTEMPLATEWRITE) JdbcTemplate write_jdbcTemplate,
            @Qualifier(DataSourceConst.JDBCTEMPLATEREAD) JdbcTemplate read_jdbcTemplate) {
        super(write_jdbcTemplate, read_jdbcTemplate);
    }
}
