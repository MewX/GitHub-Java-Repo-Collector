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