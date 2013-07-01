# Inserting preselected speakers data

# --- !Ups

INSERT INTO SPEAKER(id, name, email, bio, picture_url, twitter_id) values (
   speaker_seq.nextval,
   'Guillaume Bort',
   'guillaumebort@someemail.com',
   'Guillaume Bort is the co-founder of Zenexity, the french Web Oriented Architecture company. Former J2EE expert, he worked several years on constructing Web frameworks for large scale companies including banks, until he decided to sum up his experience by creating Play framework focusing back on simplicity. He still leads development of the Play framework.',
   'https://secure.gravatar.com/avatar/adcd749d588278dbd255068c1d4b20d3?s=200',
   'guillaumebort');

INSERT INTO SUBMISSION(id, title, proposal, is_approved, keywords, speaker_id) values (
	submission_seq.nextval,
	'History of playframework',
	'Walk down the memory lane of how playframework got started',
	true,
	'play1,play2',
	currval('SPEAKER_SEQ')
);

INSERT INTO SPEAKER(id, name, email, bio, picture_url, twitter_id) values (
   speaker_seq.nextval,
   'Maxime Dantec',
   'Warry@someemail.com',
   'Maxime is a UI/UX designer and developer @Typesafe since 2012, he previously held the same position at Zenexity.',
   'https://secure.gravatar.com/avatar/12fda944ce03f21eb4f7f9fdd7512a8e?s=200',
   'Warry');

INSERT INTO SUBMISSION(id, title, proposal, is_approved, keywords, speaker_id) values (
	submission_seq.nextval,
	'Working with assets',
	'So you want to build html5 application with play? I can help you with how to integrate various types of assets with Play',
	true,
	'play2,html5,assets',
	currval('SPEAKER_SEQ')
);

INSERT INTO SPEAKER(id, name, email, bio, picture_url, twitter_id) values (
    speaker_seq.nextval,
   'Sadek Drobi',
   'sadache@someemail.com', 
   'Sadek Drobi, CTO of Zenexity, a software engineer specialized in design and implementation of enterprise applications with a particular focus on bridging the gap between the problem domain and the solution domain. As a core Play developer, he works on the architecture, design and implementation of the framework.',
   'https://secure.gravatar.com/avatar/d349588ba91256515f7e2aa315e8cfae?s=200',
   'sadache');
   
INSERT INTO SUBMISSION(id, title, proposal, is_approved, keywords, speaker_id) values (
	submission_seq.nextval,
	'I see your async and raise it with reactive',
	'Async is boring lets make it more reactive',
	true,
	'play2,reactive,iteratee,enumerator',
	currval('SPEAKER_SEQ')
);
   
   
INSERT INTO SPEAKER(id, name, email, bio, picture_url, twitter_id) values (
  speaker_seq.nextval,
  'James Roper',
  'jroper@someemail.com',
  'James is has a range of experience across open source projects and enterprise applications. Having worked forAtlassian, James is passionate about providing developers with the best tools to get the job done. James is now workingfor Typesafe, with Play Framework being his primary focus.',
  'https://secure.gravatar.com/avatar/dd48848f9904bdce25eb2a2824842f50?s=200',
  'jroper');

INSERT INTO SUBMISSION(id, title, proposal, is_approved, keywords, speaker_id) values (
	submission_seq.nextval,
	'Whats new in Play 3',
	'Playframework has come a long way. In this presentation I will talk about things that are coming in next major version of play. Prepared to be blown away',
	true,
	'play2,play3',
	currval('SPEAKER_SEQ')
);

# --- !Downs

DELETE FROM SUBMISSION;

DELETE FROM SPEAKER WHERE name in ('Guillaume Bort', 'Maxime Dantec', 'Sadek Drobi', 'James Roper');