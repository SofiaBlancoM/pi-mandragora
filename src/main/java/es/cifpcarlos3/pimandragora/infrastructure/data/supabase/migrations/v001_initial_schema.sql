begin;

drop table if exists public.books cascade;
drop table if exists public.authors cascade;
drop table if exists public.categories cascade;

drop function if exists public.set_updated_at() cascade;

create extension if not exists "pgcrypto";

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create table public.authors (
  id uuid primary key default gen_random_uuid(),
  full_name text not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create unique index ux_authors_full_name on public.authors (lower(full_name));

create trigger tr_authors_set_updated_at
before update on public.authors
for each row execute function public.set_updated_at();

insert into public.authors (full_name) values
  ('George Orwell'),
  ('Jane Austen'),
  ('Isaac Asimov'),
  ('Ursula K. Le Guin'),
  ('Fyodor Dostoevsky');

create table public.categories (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  description text null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create unique index ux_categories_name on public.categories (lower(name));

create trigger tr_categories_set_updated_at
before update on public.categories
for each row execute function public.set_updated_at();

insert into public.categories (name, description) values
  ('Dystopian', 'Dystopian and political fiction'),
  ('Classic', 'Classic literature'),
  ('Science Fiction', 'Sci-fi novels and stories'),
  ('Fantasy', 'Fantasy and speculative fiction'),
  ('Philosophy', 'Philosophical and psychological works'),
  ('Satire', 'Satirical works');

create table public.books (
  id uuid primary key default gen_random_uuid(),

  isbn text not null,
  title text not null,

  author_id uuid not null references public.authors(id) on delete restrict,
  category_id uuid not null references public.categories(id) on delete restrict,

  publisher text null,
  publication_date date null,

  price numeric(10,2) not null check (price >= 0),
  stock integer not null default 0 check (stock >= 0),

  status text not null default 'ACTIVE'
    check (status in ('ACTIVE', 'OUT_OF_STOCK', 'DISCONTINUED')),

  cover_image_path text null,
  cover_image_updated_at timestamptz null,

  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create unique index ux_books_isbn on public.books (isbn);
create index ix_books_author_id on public.books (author_id);
create index ix_books_category_id on public.books (category_id);
create index ix_books_title on public.books (lower(title));

create trigger tr_books_set_updated_at
before update on public.books
for each row execute function public.set_updated_at();

insert into public.books (
  isbn, title, author_id, category_id, publisher, publication_date, price, stock, status,
  cover_image_path, cover_image_updated_at
)
values
(
  '9780451524935',
  '1984',
  (select id from public.authors where lower(full_name) = lower('George Orwell') limit 1),
  (select id from public.categories where lower(name) = lower('Dystopian') limit 1),
  'Secker & Warburg',
  date '1949-06-08',
  12.99,
  15,
  'ACTIVE',
  null,
  null
),
(
  '9780451526342',
  'Animal Farm',
  (select id from public.authors where lower(full_name) = lower('George Orwell') limit 1),
  (select id from public.categories where lower(name) = lower('Satire') limit 1),
  'Secker & Warburg',
  date '1945-08-17',
  9.99,
  8,
  'ACTIVE',
  null,
  null
),
(
  '9780141439518',
  'Pride and Prejudice',
  (select id from public.authors where lower(full_name) = lower('Jane Austen') limit 1),
  (select id from public.categories where lower(name) = lower('Classic') limit 1),
  'T. Egerton, Whitehall',
  date '1813-01-28',
  10.50,
  12,
  'ACTIVE',
  null,
  null
),
(
  '9780553293357',
  'Foundation',
  (select id from public.authors where lower(full_name) = lower('Isaac Asimov') limit 1),
  (select id from public.categories where lower(name) = lower('Science Fiction') limit 1),
  'Gnome Press',
  date '1951-06-01',
  14.25,
  6,
  'ACTIVE',
  null,
  null
),
(
  '9780441478125',
  'The Left Hand of Darkness',
  (select id from public.authors where lower(full_name) = lower('Ursula K. Le Guin') limit 1),
  (select id from public.categories where lower(name) = lower('Science Fiction') limit 1),
  'Ace Books',
  date '1969-03-01',
  13.40,
  0,
  'OUT_OF_STOCK',
  null,
  null
),
(
  '9780374528379',
  'The Brothers Karamazov',
  (select id from public.authors where lower(full_name) = lower('Fyodor Dostoevsky') limit 1),
  (select id from public.categories where lower(name) = lower('Philosophy') limit 1),
  'The Russian Messenger',
  date '1880-11-01',
  16.99,
  4,
  'ACTIVE',
  null,
  null
);

commit;
