insert into storage.buckets (id, name, public)
values ('book-covers', 'book-covers', false)
on conflict (id) do update
set name = excluded.name,
    public = excluded.public;


alter table storage.objects enable row level security;



drop policy if exists "book-covers select (auth)" on storage.objects;
create policy "book-covers select (auth)"
on storage.objects
for select
to authenticated
using (bucket_id = 'book-covers');


drop policy if exists "book-covers insert (auth)" on storage.objects;
create policy "book-covers insert (auth)"
on storage.objects
for insert
to authenticated
with check (bucket_id = 'book-covers');


drop policy if exists "book-covers update (auth)" on storage.objects;
create policy "book-covers update (auth)"
on storage.objects
for update
to authenticated
using (bucket_id = 'book-covers')
with check (bucket_id = 'book-covers');


drop policy if exists "book-covers delete (auth)" on storage.objects;
create policy "book-covers delete (auth)"
on storage.objects
for delete
to authenticated
using (bucket_id = 'book-covers');