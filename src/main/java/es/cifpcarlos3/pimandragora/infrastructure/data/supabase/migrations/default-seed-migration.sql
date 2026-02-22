begin;

    delete from public.books;
    delete from public.authors;
    delete from public.categories;

    -- Categorías
    insert into public.categories (name, description)
    values
      ('Fantasía', 'Historias de mundos imaginarios, magia y aventuras.'),
      ('Fantasía urbana', 'Fantasía ambientada en ciudades o entornos contemporáneos.'),
      ('Misterio', 'Relatos centrados en enigmas, investigación y tensión.'),
      ('Clásicos', 'Obras canónicas de la literatura universal.'),
      ('Poesía', 'Colecciones poéticas y obras líricas.'),
      ('Ficción histórica', 'Narrativas ambientadas en épocas históricas.'),
      ('Ciencia ficción', 'Especulación científica y mundos futuros.')
    on conflict (lower(name)) do nothing;

    -- Autores
    insert into public.authors (full_name, bio)
    values
      ('Osamu Dazai', 'Autor japonés. Su nombre inspira al personaje Dazai en Bungou Stray Dogs.'),
      ('Atsushi Nakajima', 'Autor japonés. Su nombre inspira al protagonista Atsushi.'),
      ('Ryunosuke Akutagawa', 'Autor japonés. Inspiración del personaje Akutagawa.'),
      ('Doppo Kunikida', 'Autor japonés. Inspiración del personaje Kunikida.'),
      ('Kenji Miyazawa', 'Autor japonés. Inspiración del personaje Kenji.'),
      ('Edgar Allan Poe', 'Escritor estadounidense, referente del misterio. Inspiración del personaje Poe.'),
      ('Howard Phillips Lovecraft', 'Autor estadounidense de horror cósmico. Inspiración del personaje Lovecraft.'),
      ('Fyodor Dostoevsky', 'Novelista ruso. Inspiración del personaje Dostoyevsky.'),
      ('Nikolai Gogol', 'Escritor ruso. Inspiración del personaje Gogol.'),
      ('Ivan Turgenev', 'Autor ruso. Inspiración del personaje Turgenev.'),
      ('Louisa May Alcott', 'Autora estadounidense. Inspiración del personaje Alcott.'),

      ('Sarah J. Maas', 'Autora de fantasía contemporánea, conocida por sagas de alto ritmo y romance.'),
      ('Patrick Rothfuss', 'Autor de fantasía épica, reconocido por su prosa y construcción de mundo.'),
      ('Laura Gallego', 'Autora española de fantasía y juvenil, muy influyente en el ámbito hispano.'),

      ('Leigh Bardugo', 'Autora relacionada por afinidad de fantasía oscura y universos serializados.'),
      ('Holly Black', 'Autora de fantasía (especialmente fae) con tonos oscuros.'),
      ('Brandon Sanderson', 'Autor de fantasía épica, sistemas de magia y universos conectados.'),
      ('Neil Gaiman', 'Autor relacionado por fantasía urbana y mitológica.')
    on conflict (lower(full_name)) do nothing;

    -- Libros
    insert into public.books
      (isbn, title, author_id, category_id, publisher, publication_date, price, stock, status, cover_image_path, cover_image_updated_at)
    values
      -- Sarah J. Maas
      ('9788408153290', 'Trono de cristal', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2013-08-07', 19.95, 25, 'ACTIVE', NULL, NULL),
      ('9788408153306', 'Corona de medianoche', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2014-02-12', 19.95, 18, 'ACTIVE', NULL, NULL),
      ('9788408153313', 'Heredera de fuego', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2015-03-11', 21.95, 16, 'ACTIVE', NULL, NULL),
      ('9788408153320', 'Reina de sombras', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2016-03-09', 21.95, 14, 'ACTIVE', NULL, NULL),
      ('9788408153337', 'Imperio de tormentas', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2017-03-08', 23.95, 12, 'ACTIVE', NULL, NULL),
      ('9788408153344', 'Torre del alba', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2018-03-07', 23.95, 10, 'ACTIVE', NULL, NULL),
      ('9788408153351', 'Reino de cenizas', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2019-03-06', 24.95, 10, 'ACTIVE', NULL, NULL),
      ('9788417834016', 'Una corte de rosas y espinas', (select id from public.authors where lower(full_name)=lower('Sarah J. Maas')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Planeta', '2016-11-15', 20.95, 22, 'ACTIVE', NULL, NULL),

      -- Patrick Rothfuss
      ('9788401337208', 'El nombre del viento', (select id from public.authors where lower(full_name)=lower('Patrick Rothfuss')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Plaza & Janés', '2009-06-18', 22.90, 20, 'ACTIVE', NULL, NULL),
      ('9788401337215', 'El temor de un hombre sabio', (select id from public.authors where lower(full_name)=lower('Patrick Rothfuss')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Plaza & Janés', '2011-03-01', 24.90, 14, 'ACTIVE', NULL, NULL),
      ('9788401337222', 'La música del silencio', (select id from public.authors where lower(full_name)=lower('Patrick Rothfuss')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Plaza & Janés', '2014-11-18', 16.90, 11, 'ACTIVE', NULL, NULL),

      -- Laura Gallego
      ('9788467559950', 'Memorias de Idhún I: La Resistencia', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2004-04-01', 14.95, 30, 'ACTIVE', NULL, NULL),
      ('9788467560154', 'Memorias de Idhún II: Tríada', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2005-04-01', 14.95, 24, 'ACTIVE', NULL, NULL),
      ('9788467561113', 'Memorias de Idhún III: Panteón', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2006-04-01', 14.95, 20, 'ACTIVE', NULL, NULL),
      ('9788467561267', 'La emperatriz de los etéreos', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2007-04-01', 13.95, 18, 'ACTIVE', NULL, NULL),
      ('9788467562066', 'Las crónicas de la Torre I: El Valle de los Lobos', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2000-04-01', 12.95, 28, 'ACTIVE', NULL, NULL),
      ('9788467562103', 'Las crónicas de la Torre II: La maldición del Maestro', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2001-04-01', 12.95, 22, 'ACTIVE', NULL, NULL),
      ('9788467562141', 'Las crónicas de la Torre III: La llamada de los muertos', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2002-04-01', 12.95, 18, 'ACTIVE', NULL, NULL),
      ('9788467562189', 'Las crónicas de la Torre IV: Fenris, el elfo', (select id from public.authors where lower(full_name)=lower('Laura Gallego')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'SM', '2003-04-01', 12.95, 15, 'ACTIVE', NULL, NULL),

      -- Bungou Stray Dogs (autores originales)
      ('9788435019468', 'Indigno de ser humano', (select id from public.authors where lower(full_name)=lower('Osamu Dazai')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Planeta', '1948-01-01', 12.90, 12, 'ACTIVE', NULL, NULL),
      ('9788491050617', 'El ocaso', (select id from public.authors where lower(full_name)=lower('Osamu Dazai')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Alianza', '1947-01-01', 11.90, 10, 'ACTIVE', NULL, NULL),
      ('9788491049970', 'Rashōmon', (select id from public.authors where lower(full_name)=lower('Ryunosuke Akutagawa')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Alianza', '1915-01-01', 10.90, 9, 'ACTIVE', NULL, NULL),
      ('9788411481759', 'El crimen de la calle Morgue', (select id from public.authors where lower(full_name)=lower('Edgar Allan Poe')),
        (select id from public.categories where lower(name)=lower('Misterio')), 'Austral', '1841-01-01', 9.95, 20, 'ACTIVE', NULL, NULL),
      ('9788491050747', 'El cuervo', (select id from public.authors where lower(full_name)=lower('Edgar Allan Poe')),
        (select id from public.categories where lower(name)=lower('Poesía')), 'Alianza', '1845-01-01', 8.95, 16, 'ACTIVE', NULL, NULL),
      ('9788491050884', 'La llamada de Cthulhu', (select id from public.authors where lower(full_name)=lower('Howard Phillips Lovecraft')),
        (select id from public.categories where lower(name)=lower('Misterio')), 'Alianza', '1928-01-01', 10.95, 13, 'ACTIVE', NULL, NULL),
      ('9788491048867', 'En las montañas de la locura', (select id from public.authors where lower(full_name)=lower('Howard Phillips Lovecraft')),
        (select id from public.categories where lower(name)=lower('Misterio')), 'Alianza', '1936-01-01', 12.95, 11, 'ACTIVE', NULL, NULL),
      ('9788420674723', 'Crimen y castigo', (select id from public.authors where lower(full_name)=lower('Fyodor Dostoevsky')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Alianza', '1866-01-01', 13.95, 14, 'ACTIVE', NULL, NULL),
      ('9788420674730', 'Los hermanos Karamázov', (select id from public.authors where lower(full_name)=lower('Fyodor Dostoevsky')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Alianza', '1880-01-01', 15.95, 10, 'ACTIVE', NULL, NULL),
      ('9788420674747', 'Noches blancas', (select id from public.authors where lower(full_name)=lower('Fyodor Dostoevsky')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Alianza', '1848-01-01', 9.95, 18, 'ACTIVE', NULL, NULL),
      ('9788420674754', 'Almas muertas', (select id from public.authors where lower(full_name)=lower('Nikolai Gogol')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Alianza', '1842-01-01', 12.95, 9, 'ACTIVE', NULL, NULL),
      ('9788420674761', 'El capote', (select id from public.authors where lower(full_name)=lower('Nikolai Gogol')),
        (select id from public.categories where lower(name)=lower('Clásicos')), 'Alianza', '1842-01-01', 8.95, 12, 'ACTIVE', NULL, NULL),

      -- Otros autores
      ('9788417347370', 'Sombra y hueso', (select id from public.authors where lower(full_name)=lower('Leigh Bardugo')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2013-06-05', 18.95, 17, 'ACTIVE', NULL, NULL),
      ('9788417347387', 'Seis de cuervos', (select id from public.authors where lower(full_name)=lower('Leigh Bardugo')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2016-02-10', 19.95, 15, 'ACTIVE', NULL, NULL),
      ('9788420428494', 'American Gods', (select id from public.authors where lower(full_name)=lower('Neil Gaiman')),
        (select id from public.categories where lower(name)=lower('Fantasía urbana')), 'Roca', '2002-01-01', 19.90, 10, 'ACTIVE', NULL, NULL),
      ('9788413144102', 'El océano al final del camino', (select id from public.authors where lower(full_name)=lower('Neil Gaiman')),
        (select id from public.categories where lower(name)=lower('Fantasía urbana')), 'Roca', '2013-09-01', 16.90, 12, 'ACTIVE', NULL, NULL),
      ('9788417347509', 'El príncipe cruel', (select id from public.authors where lower(full_name)=lower('Holly Black')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Hidra', '2018-01-10', 18.95, 16, 'ACTIVE', NULL, NULL),
      ('9788413143914', 'El imperio final', (select id from public.authors where lower(full_name)=lower('Brandon Sanderson')),
        (select id from public.categories where lower(name)=lower('Fantasía')), 'Nova', '2007-01-01', 21.90, 12, 'ACTIVE', NULL, NULL)
    ;

commit;