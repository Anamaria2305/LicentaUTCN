PGDMP     6            	        {           evcharge    14.5    14.5     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16440    evcharge    DATABASE     l   CREATE DATABASE evcharge WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'English_United States.1252';
    DROP DATABASE evcharge;
                postgres    false            �            1259    16656    stations    TABLE     �   CREATE TABLE public.stations (
    id integer NOT NULL,
    name character varying(255),
    plug_ids character varying(255)
);
    DROP TABLE public.stations;
       public         heap    postgres    false            �            1259    16479    stations_seq    SEQUENCE     v   CREATE SEQUENCE public.stations_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.stations_seq;
       public          postgres    false            �            1259    16663    vehicles    TABLE       CREATE TABLE public.vehicles (
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
       public         heap    postgres    false            �            1259    16504    vehicles_seq    SEQUENCE     v   CREATE SEQUENCE public.vehicles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.vehicles_seq;
       public          postgres    false            �          0    16656    stations 
   TABLE DATA           6   COPY public.stations (id, name, plug_ids) FROM stdin;
    public          postgres    false    211   �       �          0    16663    vehicles 
   TABLE DATA           �   COPY public.vehicles (id, soccurrent, chr_dis_per_hour, constraints_penalty, favourite_time_slots, max_capacity, minsoc, csid) FROM stdin;
    public          postgres    false    212   �       �           0    0    stations_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.stations_seq', 1, false);
          public          postgres    false    209            �           0    0    vehicles_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.vehicles_seq', 1, false);
          public          postgres    false    210            b           2606    16662    stations stations_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.stations
    ADD CONSTRAINT stations_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.stations DROP CONSTRAINT stations_pkey;
       public            postgres    false    211            d           2606    16669    vehicles vehicles_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT vehicles_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT vehicles_pkey;
       public            postgres    false    212            e           2606    16670 $   vehicles fk7k9q1et0hkqiynp5bdf9buyr9    FK CONSTRAINT     �   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9 FOREIGN KEY (csid) REFERENCES public.stations(id);
 N   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9;
       public          postgres    false    3170    212    211            �   D   x�3�.)JLITPpKL*�L��T�+R05�4�1�2�t�IMMTp*�KT��,M�q�s��qqq 1a      �   s   x�M��� D��q-ъ0�^��:ք�	�.`&d~e0V%� P�|9�pZ�����UPw?�)\}E�����Q������ؽ^�Ů��̣���w�>����3��z"�     