/*
 * Copyright (C) 2017 B3Partners B.V.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.b3p.playbase.cron;

import nl.b3p.playbase.entities.CronJob;
import nl.b3p.playbase.stripes.ImportPlaymappingActionBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Meine Toonen
 */
public class PlaybaseJob implements Job{
    private static final Log log = LogFactory.getLog(PlaybaseJob.class);

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        JobDataMap jdm = jec.getJobDetail().getJobDataMap();
        CronJob cj = (CronJob)jdm.get(CronListener.QUARTZ_JOB_DATA_MAP_ENTITY_KEY);
        log.info("Executing playbasejob " + cj.getId() +". Type is " + cj.getType_().toString());
        CronType ct = cj.getType_();
        
        switch (ct){
            case IMPORT_PLAYADVISOR:
                importPlayadvisor(cj);
                break;
            case IMPORT_PLAYMAPPING:
                importPlaymapping(cj);
                break;
            default:
                break;
        }
    }
    
    private void importPlaymapping(CronJob job){
        ImportPlaymappingActionBean ipab = new ImportPlaymappingActionBean();
    }
    
    private void importPlayadvisor(CronJob job){
        throw new UnsupportedOperationException("Automagically importing playdvisor not yet implemented");
    }
    
}
