---------------------------------------------------------------
-- count repo type
select
  r.repo_type, count(distinct dependencies.project) as count
from dependencies
  inner join
  (select project, repo_type from repo_type) r
  on dependencies.project = r.project
group by r.repo_type;

select message, project, strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as time_stamp from commits  where date(time_stamp) between date('1997-01-01') and date('1997-12-31') order by time_stamp;

-- count number of projects based on year
select count(distinct project) as project_num, time_stamp from
  (select
  project,
  commit_tag
  from dependencies) as d
inner join (
      select message, strftime('%Y', datetime(time / 1000, 'unixepoch')) as time_stamp
      from commits
      where date(time_stamp) between date('1997') and date('2017')
      ) as c
on d.commit_tag = c.message
group by time_stamp;

-- count average number of library use based on year
select commit_time, avg(total) as average from
  (select
  project,
  commit_time,
  repo_type,
  avg(total) as total
from (select
        d.project,
        commit_time,
        r.repo_type,
        count(*) as total
      from (
          (select
             project,
             commit_tag
           from dependencies) as d
          inner join
          (select
             message,
             strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
           from commits
           where date(commit_time) between date('2017-01-01') and date('2017-03-31')) as commits
            on commit_tag = commits.message)
        inner join
        (select
           project,
           repo_type
         from repo_type) as r
          on d.project = r.project
      group by commit_tag)
group by project);

-- count number of projects based on year
select distinct d.project, time_stamp, repo_type from
  (select
  project,
  commit_tag
  from dependencies) as d
inner join (
      select message, strftime('%Y', datetime(time / 1000, 'unixepoch')) as time_stamp
      from commits
      where date(time_stamp) between date('2001') and date('2001')
      ) as c
on d.commit_tag = c.message
  inner join repo_type
  on d.project = repo_type.project;

-- count average number of library use based on year, project type
select
  project,
  commit_time,
  repo_type,
  avg(total) as total
from (select
        d.project,
        commit_time,
        r.repo_type,
        count(*) as total
      from (
          (select
             project,
             commit_tag
           from dependencies) as d
          inner join
          (select
             message,
             strftime('%Y', datetime(time / 1000, 'unixepoch')) as commit_time
           from commits
           where date(commit_time) = date('2015')) as commits
            on commit_tag = commits.message)
        inner join
        (select
           project,
           repo_type
         from repo_type) as r
          on d.project = r.project
      group by commit_tag)
group by project;

-- select most frequently used group id based on 'User' or 'Organization
select group_id, count(*) as count from
(select group_id
from
  (select project, group_id from dependencies where group_id !='' and group_id != 'no dependency') as d
inner join
  (select
        project,
        repo_type
      from repo_type
      where repo_type = 'Organization') as r
on d.project = r.project
group by d.project, group_id)
group by group_id
order by count desc
limit 20;

-- select most frequently used artifact id based on 'User' or 'Organization
select group_id, artifact_id, count(*) as count from
(select group_id, artifact_id
from
  (select project, group_id, artifact_id from dependencies where artifact_id !='' and artifact_id != 'no dependency') as d
inner join
  (select
        project,
        repo_type
      from repo_type
      where repo_type = 'Organization') as r
on d.project = r.project
group by d.project, artifact_id)
group by artifact_id
order by count desc
limit 20;

-- count average number of library use based on quarter
select commit_time, avg(total) as average from
  (select
  project,
  commit_time,
  repo_type,
  avg(total) as total
from (select
        d.project,
        commit_time,
        r.repo_type,
        count(*) as total
      from (
          (select
             project,
             commit_tag
           from dependencies) as d
          inner join
          (select
             message,
             strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
           from commits
           where date(commit_time) between date('2016-01-01') and date('2016-03-31')) as commits
            on commit_tag = commits.message)
        inner join
        (select
           project,
           repo_type
         from repo_type) as r
          on d.project = r.project
      group by commit_tag)
group by project);
---------------------------------------------------------------

-- *****count percentage of personal/organization projects*****
select
  repo_type,
  count(*) as total
from repo_type
group by repo_type;

-- select commit from a given time period
SELECT
  id,
  strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
from commits
where date(commit_time) between date('2015-07-01') and date('2015-12-31');

-- count commit based on year
SELECT
  count(*),
  strftime('%Y', datetime(time / 1000, 'unixepoch')) as commit_time
from commits
group by commit_time;

-- select dependency of each project in given time period
select
  project,
  commit_tag,
  group_id,
  artifact_id,
  version,
  commit_time
from dependencies
  inner join (SELECT
                message,
                strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
              from commits
              where date(commit_time) between date('2015-07-01') and date('2015-12-31')) as commit_2015
    on commit_tag = commit_2015.message;

-- count library use for each project in given time period
select
  project,
  commit_time,
  count(*) as total
from (select
        project,
        commit_tag,
        group_id,
        artifact_id,
        version,
        commit_time
      from dependencies
        inner join (SELECT
                      message,
                      strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
                    from commits
                    where date(commit_time) between date('2015-07-01') and date('2015-12-31')) as commit_2015
          on commit_tag = commit_2015.message)
group by commit_tag;

-- *****select average library use for each project in given time period, based on 'User' or 'Organization'*****
-- *****change the value in date(***) and date(***)*****
elect
  project,
  commit_time,
  repo_type,
  avg(total)
from (select
        d.project,
        commit_time,
        r.repo_type,
        count(*) as total
      from (
          (select
             project,
             commit_tag
           from dependencies) as d
          inner join
          (select
             message,
             strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
           from commits
           where date(commit_time) between date('2015-07-01') and date('2015-12-31')) as commit_2015
            on commit_tag = commit_2015.message)
        inner join
        (select
           project,
           repo_type
         from repo_type
         where repo_type = 'User' or repo_type = 'Organization') as r
          on d.project = r.project
      group by commit_tag)
group by project;

-- *****select most frequently used group id in given time period, based on 'User' or 'Organization'*****
-- *****change the value in date(***) and date(***)*****
select
  group_id,
  count(*) as count
from
  (select
     commit_time,
     r.repo_type,
     d.group_id
   from (
       (select
          project,
          group_id,
          commit_tag
        from dependencies
        where group_id != '') as d
       inner join
       (select
          project,
          message,
          strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
        from commits
        where date(commit_time) between date('2015-07-01') and date('2015-12-31')) as commit_2015
         on commit_tag = commit_2015.message)
     inner join
     (select
        project,
        repo_type
      from repo_type
      where repo_type = 'User' or repo_type = 'Organization') as r
       on d.project = r.project
   group by d.project, group_id)
group by group_id
order by count
  desc;

-- *****select most frequently used artifact id in given time period, based on 'User' or 'Organization'*****
select
  artifact_id,
  count(*) as count
from
  (select
     d.project,
     commit_time,
     r.repo_type,
     d.artifact_id
   from (
       (select
          project,
          artifact_id,
          commit_tag
        from dependencies
        where group_id != '') as d
       inner join
       (select
          project,
          message,
          strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as commit_time
        from commits
        where date(commit_time) between date('2015-07-01') and date('2015-12-31')) as commit_2015
         on commit_tag = commit_2015.message)
     inner join
     (select
        project,
        repo_type
      from repo_type
      where repo_type = 'User' or repo_type = 'Organization') as r
       on d.project = r.project
   group by d.project, artifact_id)
group by artifact_id
order by count
  desc;