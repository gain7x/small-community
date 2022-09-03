INSERT INTO member(id, email, last_password_change, member_role, nickname, password)
VALUES (1, 'userA@mail.com', NOW(), 'USER', 'userA', 'password');

INSERT INTO category(id, code, enable, name) VALUES (1, 'dev', true, '개발');

INSERT INTO content(id, member_id)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (4, 1),
       (5, 1),
       (6, 1),
       (7, 1),
       (8, 1);

INSERT INTO main_text(id, text, writer_id)
VALUES (1, 'some text', 1),
       (2, 'some text', 1),
       (3, 'some text', 1),
       (4, 'some text', 1),
       (5, 'some text', 1),
       (6, 'other text', 1),
       (7, 'other text', 1),
       (8, 'other text', 1);

INSERT INTO post(id, enable, nickname, title, views, votes, category_id, content_id, main_text_id, writer_id)
VALUES (1, true, 'userA', 'some title', 0, 0, 1, 1, 1, 1),
       (2, true, 'userA', 'some title', 0, 0, 1, 2, 2, 1),
       (3, true, 'userA', 'some title', 0, 0, 1, 3, 3, 1),
       (4, true, 'userA', 'some title', 0, 0, 1, 4, 4, 1),
       (5, true, 'userA', 'some title', 0, 0, 1, 5, 5, 1),
       (6, true, 'userA', 'other title', 0, 0, 1, 6, 6, 1),
       (7, true, 'userA', 'other title', 0, 0, 1, 7, 7, 1),
       (8, true, 'userA', 'other title', 0, 0, 1, 8, 8, 1);