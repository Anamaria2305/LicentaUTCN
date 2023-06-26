PGDMP         7                {           evcharge    15.3    15.3                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16419    evcharge    DATABASE     �   CREATE DATABASE evcharge WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE evcharge;
                postgres    false            �            1259    16674    drivers    TABLE     �   CREATE TABLE public.drivers (
    id integer NOT NULL,
    password character varying(255),
    username character varying(255),
    phone character varying(255)
);
    DROP TABLE public.drivers;
       public         heap    postgres    false            �            1259    16681    drivers_seq    SEQUENCE     u   CREATE SEQUENCE public.drivers_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 "   DROP SEQUENCE public.drivers_seq;
       public          postgres    false            �            1259    16711    schedule    TABLE     �   CREATE TABLE public.schedule (
    id integer NOT NULL,
    value_charged integer,
    car_id integer,
    "time" integer,
    csid integer
);
    DROP TABLE public.schedule;
       public         heap    postgres    false            �            1259    16700    schedule_seq    SEQUENCE     v   CREATE SEQUENCE public.schedule_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.schedule_seq;
       public          postgres    false            �            1259    16575    stations    TABLE     �   CREATE TABLE public.stations (
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
       public          postgres    false            �            1259    16580    vehicles    TABLE     s  CREATE TABLE public.vehicles (
    id integer NOT NULL,
    soccurrent integer,
    chr_dis_per_hour integer,
    constraints_penalty character varying(255),
    favourite_time_slots character varying(255),
    max_capacity integer,
    minsoc integer,
    csid integer,
    plate_number character varying(255),
    model character varying(255),
    driver_id integer
);
    DROP TABLE public.vehicles;
       public         heap    postgres    false            �            1259    16431    vehicles_seq    SEQUENCE     v   CREATE SEQUENCE public.vehicles_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.vehicles_seq;
       public          postgres    false                      0    16674    drivers 
   TABLE DATA           @   COPY public.drivers (id, password, username, phone) FROM stdin;
    public          postgres    false    218   ?                 0    16711    schedule 
   TABLE DATA           K   COPY public.schedule (id, value_charged, car_id, "time", csid) FROM stdin;
    public          postgres    false    221   �                 0    16575    stations 
   TABLE DATA           6   COPY public.stations (id, name, plug_ids) FROM stdin;
    public          postgres    false    216   q!                 0    16580    vehicles 
   TABLE DATA           �   COPY public.vehicles (id, soccurrent, chr_dis_per_hour, constraints_penalty, favourite_time_slots, max_capacity, minsoc, csid, plate_number, model, driver_id) FROM stdin;
    public          postgres    false    217   "                  0    0    drivers_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.drivers_seq', 401, true);
          public          postgres    false    219                       0    0    schedule_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.schedule_seq', 1551, true);
          public          postgres    false    220                       0    0    stations_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.stations_seq', 1, false);
          public          postgres    false    214                       0    0    vehicles_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.vehicles_seq', 1151, true);
          public          postgres    false    215            y           2606    16680    drivers drivers_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.drivers
    ADD CONSTRAINT drivers_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.drivers DROP CONSTRAINT drivers_pkey;
       public            postgres    false    218            {           2606    16715    schedule schedule_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.schedule DROP CONSTRAINT schedule_pkey;
       public            postgres    false    221            u           2606    16586    stations stations_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.stations
    ADD CONSTRAINT stations_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.stations DROP CONSTRAINT stations_pkey;
       public            postgres    false    216            w           2606    16588    vehicles vehicles_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT vehicles_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT vehicles_pkey;
       public            postgres    false    217            |           2606    16589 $   vehicles fk7k9q1et0hkqiynp5bdf9buyr9    FK CONSTRAINT     �   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9 FOREIGN KEY (csid) REFERENCES public.stations(id);
 N   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT fk7k9q1et0hkqiynp5bdf9buyr9;
       public          postgres    false    3189    216    217            }           2606    16682 $   vehicles fkaashphrwfd4ts511y8vj785ia    FK CONSTRAINT     �   ALTER TABLE ONLY public.vehicles
    ADD CONSTRAINT fkaashphrwfd4ts511y8vj785ia FOREIGN KEY (driver_id) REFERENCES public.drivers(id);
 N   ALTER TABLE ONLY public.vehicles DROP CONSTRAINT fkaashphrwfd4ts511y8vj785ia;
       public          postgres    false    217    3193    218            ~           2606    16716 $   schedule fkq859sjqc4ml6slm2culvqfln4    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT fkq859sjqc4ml6slm2culvqfln4 FOREIGN KEY (car_id) REFERENCES public.vehicles(id);
 N   ALTER TABLE ONLY public.schedule DROP CONSTRAINT fkq859sjqc4ml6slm2culvqfln4;
       public          postgres    false    217    221    3191                       2606    16721 $   schedule fks37pp9gd8r6i0flbit5mfn0n1    FK CONSTRAINT     �   ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT fks37pp9gd8r6i0flbit5mfn0n1 FOREIGN KEY (csid) REFERENCES public.stations(id);
 N   ALTER TABLE ONLY public.schedule DROP CONSTRAINT fks37pp9gd8r6i0flbit5mfn0n1;
       public          postgres    false    221    3189    216               V   x�360�442615�L�+-�J,�Ht�L����K���40770�4120�260����/�DSefhjbdf�el
7�$���Q&\1z\\\ ��         �  x�E�[c!��^fJ�2�_�p�����ʕF�dN�Uяzlѐ�0E],�\�&�א������N�6���=w�����d}ܑ:������8qJ2}���/�A�÷�l=nM�m|2]��o����o��}ڂ��H?�i񗾂�/�.7J]v�:\=>b2t�Щ�I]v)�@���l������m\O�L��p����5�])���\�^��2p�ǎ��3&g����Ő�����_r|�J�,8Vu�b hо>�E�D�Xթ�,HD�gy�]��
4�'��.L6�~�ųG]��ٜ������tzl��H�-c^��!g�!�霏�����s�V��۪����̚e~9��Z�������4;�;�|�ݜg�Lt�%�6��!O�$�1;����]T��f���������         �   x�5˱�0@��n�����`\С4Gr�%�B�@��b����?��Y^\W��,�e��^����h�T
����Yye����R���|��E���{{h�&z��7�Ӥ�閔�m}�83��#�g�'V         �  x����n�@�������̙ےK�&T�D��T�T�E�E߾3����a�dm}����< @
�B�J@�%��4�-��+b���j=��q�������A�
��J)��r�C7�L{�,�w0'Nւ:�-(��ĺ�v{��4���$B���t��<p����1�\���b9��:�1����e��4N���-����m���5BA�l�����Q�B����C�E�5D������:�B��N�*ѹ��d�G�^��6��6��$T�&!�NB�����SVz6�/�$.�gU�����)H��?"�xGK)��R�u�T�y��#��luNe;(���q�Z�o�ޏ��/���}-脻�����I�?V=%�SFݎ���ϛ�1�O�`G�X/��H�vKl��YZ��1��Q�xn/$�.U��1L�]��r4���!�6[�3�5���P��
C�Ŧ�9�ڷ���l�e��~�0���K��3sb�]���8�%t�K�;�Ӭ�dR�b��z�gN;���\�����˛\�7�G��=<Du�Iu�����>UR��D�6��j����pq5-Ӧ��spZ��)삫x��̇C�\�T��\R�|p�w�L�,N���P<g�%�})��?>/��     