PGDMP         4                {           evcharge    15.3    15.3                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16419    evcharge    DATABASE     �   CREATE DATABASE evcharge WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE evcharge;
                postgres    false            �            1259    16441    stations    TABLE     �   CREATE TABLE public.stations (
    id integer NOT NULL,
    name character varying(255),
    plug_ids character varying(255)
);
    DROP TABLE public.stations;
       public         heap    postgres    false            �            1259    16425    stations_seq    SEQUENCE     v   CREATE SEQUENCE public.stations_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.stations_seq;
       public          postgres    false            �            1259    16446    vehicles    TABLE       CREATE TABLE public.vehicles (
    id integer NOT NULL,
    soccurrent integer,
    chr_dis_per_hour integer,
    constraints_penalty character varying(255),
    favourite_time_slots character varying(255),
    max_capacity integer,
    minsoc integer,
    csid integer
);
    DROP TABLE public.vehicles;
       public         heap    postgres    false            �            1259    16431    vehicles_seq    SEQUENCE     v   CREATE SEQUENCE public.vehicles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.vehicles_seq;
       public          postgres    false            �          0    16441    stations 
   TABLE DATA           6   COPY public.stations (id, name, plug_ids) FROM stdin;
    public          postgres    false    216   �                  0    16446    vehicles 
   TABLE DATA           �   COPY public.vehicles (id, soccurrent, chr_dis_per_hour, constraints_penalty, favourite_time_slots, max_capacity, minsoc, csid) FROM stdin;
    public          postgres    false    217   E                  0    0    stations_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.stations_seq', 1, false);
          public          postgres    false    214                       0    0    vehicles_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.vehicles_seq', 1, false);
          public          postgres    false    215            k           2606    16452    stations stations_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.stations
    ADD CONSTRAINT stations_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.stations DROP CONSTRAINT stations_pkey;
       public            postgres    false    216            m           2606    16454    vehicles vehicles_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT vehicles_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT vehicles_pkey;
       public            postgres    false    217            n           2606    16455 $   vehicles fk7k9q1et0hkqiynp5bdf9buyr9    FK CONSTRAINT     �   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9 FOREIGN KEY (csid) REFERENCES public.stations(id);
 N   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9;
       public          postgres    false    216    3179    217            �   x   x�5̱�0��n�?����\"�C4Gr�%'�,��X%�������$�E5�9c�h;�;ρNEU��"�e���@�E��[ї��
�Ǡ���A��1Q�Zn��y{ϫ�HԹ�w��o~"~          q  x���K��0D�pk���]����e:Oj����@U6����Xb�G�9��Qc�a�L��u�� CmI�w�ߐS�Gb�=���BZ�Q�8�d@��>.��9μ�]r;����8WQb_�<�}"�d�#�F>W�Fv���`�:LLQ^q��34CV��L�ε߽�/�@�1PǦ~�zeb��X�]#Ȑ�a�C.]3��Md:!�M&�܄�h3��C�!bO(�Ђ}�;��1��v%�p3vlu���>����뭏�B���=�0��Kf�cf�H�z#a�]M���`����Ps���[8�@7���t���i���xj�(��G�}x�.|ga%��B���c�ū����GU� �^!�     