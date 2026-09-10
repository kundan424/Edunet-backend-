--
-- PostgreSQL database dump
--

\restrict 9rAFX0PopqAwuT0Gcz5lDjCHGwKoKPnyEQiHWlNKch9tECfyZ6YgidDs6DbV2ay

-- Dumped from database version 16.15
-- Dumped by pg_dump version 16.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.users (id, name, email, password_hash, role, status, created_at, updated_at) FROM stdin;
a7534d1f-47ea-4376-ba52-a4963360a8ac	Smoke Test Instructor	instructor.smoke@example.com	$2a$10$oQQ4pIoA5yvacsv4EpaCVOA6I5DNVK0jlHqfA40IbnSp.VrrZJe2G	INSTRUCTOR	ACTIVE	2026-09-04 18:10:20.985184	2026-09-04 18:10:20.985223
2a51b322-cdfe-49af-bd6b-6cc0f2aca58a	Smoke Test Student	student.smoke@example.com	$2a$10$IHZ3t7vBaJvHlgzJc01v0OHw.ipEAbGRLtTw6jgxkjKmb9ilcIg5.	STUDENT	ACTIVE	2026-09-04 18:10:21.526507	2026-09-04 18:10:21.526533
47d97cb0-770a-413d-9b12-a92567ef3659	Smoke Test Admin	admin.smoke@example.com	$2a$10$Xqpd/fwnGDhZ88LK8AXsH.fwLN2FitdSvxI073FPXsMdOzz50utPa	ADMIN	ACTIVE	2026-09-04 18:10:21.722574	2026-09-04 18:10:21.72262
83b824ee-e07b-4861-9ebc-5ec4ce928de1	Admin Smoke	admin.smoke.17885527799e57@example.com	$2a$10$38PCrITJNVvrp.u/QVxgiextCg7JFgXJAsd8Adl.tITelx8aDuvda	ADMIN	ACTIVE	2026-09-04 20:12:59.97777	2026-09-04 20:12:59.977812
23e76988-9506-4943-8560-30ebd57b648e	Inst Smoke	inst.smoke.178855278035be@example.com	$2a$10$CjjHpZGqT9/QVYi0UIebZeEVEcB.0ANtqZF0gnm7MXipfboZq52a2	INSTRUCTOR	ACTIVE	2026-09-04 20:13:00.450326	2026-09-04 20:13:00.450347
1c4e3e1c-6cef-4844-a423-04fa082ac6eb	Test	test990b2ade-9519-4fb4-9b9a-7148c80870b4@test.com	$2a$10$s1wjZPBBOMBSW2DVH.1q7ehRmH6Dt8Uont3BcgE31MUFP5IiPSrcG	INSTRUCTOR	ACTIVE	2026-09-08 22:52:27.338355	2026-09-08 22:52:27.338405
2e513123-df2e-4654-aa6f-7a13974a96d4	Test	test846a1a86-7875-4d39-9b8d-c4cde06a227f@test.com	$2a$10$dmqI2GyVs4nFeu0QxkiF4O1Bj3fkz.2/WrKZm1jJ8YS2tAFNcU5IG	INSTRUCTOR	ACTIVE	2026-09-08 22:52:36.307959	2026-09-08 22:52:36.307987
400f5b84-780f-46d5-b3b4-9b9ee5db917b	Admin Smoke	admin.smoke.17885528090814@example.com	$2a$10$1K0MqQAcfuHcXgZFxNZwGOpHADLnYopgP8te34qVTLBe0qqLH8Zfi	ADMIN	ACTIVE	2026-09-04 20:13:29.510852	2026-09-04 20:13:29.510872
dddf701e-772c-4367-a5db-70bf01ab408c	Inst Smoke	inst.smoke.1788552809eb3e@example.com	$2a$10$80lZiC1bmbhHS7j/Wlj1dudnc8N9aOzFmEu0m7/1j.MWSBpF5dVq6	INSTRUCTOR	ACTIVE	2026-09-04 20:13:29.786901	2026-09-04 20:13:29.786919
42cd768d-6592-40f4-bb4a-a76236102766	Test	testfa198289-2bd6-41d5-94d8-c9633c104d1f@test.com	$2a$10$NIukwVFh3HtdGcgyy5nCzu/ATaZPg/u7rzkNO5tIdz7jDtISvVoJi	INSTRUCTOR	ACTIVE	2026-09-08 22:52:48.116324	2026-09-08 22:52:48.116352
f9bb6cfd-4d71-4c86-ab23-0c661eb6d2be	Test Instructor	inst_92eae4f4-5403-4537-b869-f8d2c8a6c44c@test.com	$2a$10$v/k8cC3h9KTVuV.QcQMwj.gxQSfC/.Ce2t4YL2lLijyRP/Meb7iN.	INSTRUCTOR	ACTIVE	2026-09-08 22:53:14.5047	2026-09-08 22:53:14.504728
21ff23be-85b7-4d50-98a4-0b50ead40696	Admin Smoke	admin.smoke.17885528429482@example.com	$2a$10$BwIJ4fBoWrJ9iPJk7qnk3.H5LNsnrjACi/41b.47wcggzpCTvm/D.	ADMIN	ACTIVE	2026-09-04 20:14:02.415729	2026-09-04 20:14:02.415742
0771c509-7296-4f5d-b62f-db8c4efa313d	Inst Smoke	inst.smoke.178855284279be@example.com	$2a$10$JTPv0prvSjvOwcycKaVyce9ZQID2gHoDSB6nAoQa.L0.vr7MNtWkO	INSTRUCTOR	ACTIVE	2026-09-04 20:14:02.696887	2026-09-04 20:14:02.696903
0e4479ad-4f5d-460e-b40b-ce6f54f036c1	Test Instructor	inst_a8193235-614f-4404-8fb0-191c99d514a9@test.com	$2a$10$hOyAkF.JmoSLmCQ1J5ItLet0tqWhYK2RoWmN5tQG91INd3WXxB8xq	INSTRUCTOR	ACTIVE	2026-09-08 22:53:31.678099	2026-09-08 22:53:31.678131
d8c5bd6d-62f8-40a1-b58b-264d22327f2b	Test Instructor	inst_ef3d4ea5-325d-4976-b13d-cb9746f7d344@test.com	$2a$10$PqIUI4fMve7IYDifQe5s8ui8cDtlx6ZLR5WTDuVfFP1n96lRwDWL.	INSTRUCTOR	ACTIVE	2026-09-08 22:53:52.347173	2026-09-08 22:53:52.347197
f2102d30-e8df-4c74-b02e-714a0027baa2	Admin Smoke	admin.smoke.17885529132aa4@example.com	$2a$10$be4whGEqhE4bA6/Age4nd.kc/t74yeBuvg7L5gOx9fegkznWroaSa	ADMIN	ACTIVE	2026-09-04 20:15:14.051688	2026-09-04 20:15:14.051702
a7b91af0-1da2-4116-b457-d848f6220f0c	Inst Smoke	inst.smoke.17885529140672@example.com	$2a$10$Wd6T/rfHRMcJkuf9EQuwpuCyLKWGej4gPYFCYFHlWcXJe3AaF0gAS	INSTRUCTOR	ACTIVE	2026-09-04 20:15:14.319908	2026-09-04 20:15:14.319919
3bbf9489-1e1a-4d6b-b319-0113907b00d2	Studenta Smoke	studentA.smoke.17885529146e29@example.com	$2a$10$g.THIaIU5i1qk3X8ZSRpTu5tm2L8WpOUmLwl57fmGQOX2xc9zuwh6	STUDENT	ACTIVE	2026-09-04 20:15:14.674442	2026-09-04 20:15:14.674454
728ead70-fc1c-444c-9116-4fe0adb36bcb	Studentb Smoke	studentB.smoke.17885529145443@example.com	$2a$10$csf4nlVIyj1Oz8jloujpTeaEeDlosZc6WqJvD1zuxZ6edCOSD3KeG	STUDENT	ACTIVE	2026-09-04 20:15:14.817413	2026-09-04 20:15:14.817427
ef7f3d31-54f4-4159-837d-68b41543e5f7	Admin Smoke	admin.smoke.1788552984cc0b@example.com	$2a$10$rstEqublCcBOibUOrHOowun.Aw//fjyOcwRS9z9jumEPZDtryDoFi	ADMIN	ACTIVE	2026-09-04 20:16:24.163711	2026-09-04 20:16:24.163727
cd2a9bf4-6df5-4dd8-9b7b-a0f482d16376	Inst Smoke	inst.smoke.17885529848dce@example.com	$2a$10$6oei1a4ak5waNLbfD0Rv1eYU6tKUawiJdPIb5uZvRMVQiE7qrRvSy	INSTRUCTOR	ACTIVE	2026-09-04 20:16:24.442555	2026-09-04 20:16:24.442569
690b7912-0daf-40a8-a7f4-081f0a87bc82	Studenta Smoke	studentA.smoke.17885529841f16@example.com	$2a$10$foYEheI50xwddMAY6jALy.vEN9Ui3xPQ5gEPZtLMRbxMqLPqJ8XTm	STUDENT	ACTIVE	2026-09-04 20:16:24.886654	2026-09-04 20:16:24.886667
fcddae91-906a-4301-8856-732f73acff07	Studentb Smoke	studentB.smoke.178855298420bc@example.com	$2a$10$YELEPENQdzPdqEi9sb2pi.V0rtMwpiGaZnR9iKWz82t5fjLx0Oiqy	STUDENT	ACTIVE	2026-09-04 20:16:25.032322	2026-09-04 20:16:25.032337
80730d89-2aba-4f89-b99a-baf9a8fe7880	Admin Smoke	admin.smoke.1788553132b491@example.com	$2a$10$HPtlDRTl5M0ZH0MJnUHMz..vlpv5732BL48j4YFX.s.Qxa5hvL0q2	ADMIN	ACTIVE	2026-09-04 20:18:52.825758	2026-09-04 20:18:52.825785
5b72b54a-a7de-433b-bd39-2e84074ec73b	Inst Smoke	inst.smoke.17885531338426@example.com	$2a$10$I2jVUujvpYlXiKVDa21X7uHGXSFAS5WiQF1yHIpv5GFzxqi2EJACC	INSTRUCTOR	ACTIVE	2026-09-04 20:18:53.103386	2026-09-04 20:18:53.103403
d5b14a7a-fcc2-44e2-9f11-58e9925e5e18	Studenta Smoke	studentA.smoke.1788553133cbd8@example.com	$2a$10$6WQEtEKp2Uiut7uJE5aFue6TtFYft8QKbNHr026bcCz.Wr0q4qbyW	STUDENT	ACTIVE	2026-09-04 20:18:53.566581	2026-09-04 20:18:53.566599
abe649c1-4ba9-4a41-a4e9-3ccaa43f1468	Studentb Smoke	studentB.smoke.178855313355fd@example.com	$2a$10$QhN1.5jl3DbpbkkzxT6nju/jEWa1olHrJ4ZZBCnJ8XympksrJbgP.	STUDENT	ACTIVE	2026-09-04 20:18:53.709356	2026-09-04 20:18:53.709372
d3e5a2c8-5f5d-44fa-827e-8d06852425b6	Smoke Student	student.smoke.prog.1788551627@example.com	$2a$10$eGeXIs5CMD6l3XwW98PTnOE77aeWirz4PGsXqTshKLI9IQEKMdonu	STUDENT	ACTIVE	2026-09-04 19:53:48.087267	2026-09-04 19:53:48.087315
677f5ccf-5f74-4690-bee2-d70644edacd3	Admin Smoke	admin.smoke.1788554649000e@example.com	$2a$10$Is1O.w.d2ouD26IREj7EDO477qQMwfATGgFsgKpguzKmLRY8pCARO	ADMIN	ACTIVE	2026-09-04 20:44:10.123024	2026-09-04 20:44:10.123061
fac32528-8b91-4aa4-a6ac-87e3d6bbdb29	Inst Smoke	inst.smoke.178855465016bf@example.com	$2a$10$SbWdCfLDdqGiuMf6eg42geUE1XgxM5uix3g2ht0CQ0xf54NbQU8xO	INSTRUCTOR	ACTIVE	2026-09-04 20:44:10.596111	2026-09-04 20:44:10.596131
6c661224-f75a-41d0-ac69-32eed59e4130	Studenta Smoke	studentA.smoke.17885546517dd6@example.com	$2a$10$f6YeC0yT5H/h4jODu.tD4uCdCEAGq0R6wsRXOWL8uXLeg1tFuFyFu	STUDENT	ACTIVE	2026-09-04 20:44:11.29005	2026-09-04 20:44:11.290066
0cf37b2e-7945-46ba-9ba7-c03b005bddf9	Studentb Smoke	studentB.smoke.1788554651f7b0@example.com	$2a$10$qG7nMb2.Tm9tM1oCSx9gfOpFXmn4RXNXJqbzFbQ/XcwfKQ26XM9O2	STUDENT	ACTIVE	2026-09-04 20:44:11.434497	2026-09-04 20:44:11.434515
a998e8b1-2a78-48d3-ba98-5ee77077de33	admin User	admin.smoke.1788554741@example.com	$2a$10$6sfbJ9Y5H5sERvWEIz88Ye0WsWgUl2JUoPnkDZB70OjVltuyjpF8m	ADMIN	ACTIVE	2026-09-04 20:45:41.611822	2026-09-04 20:45:41.611844
e232f613-d676-4993-902f-be15a9529c3b	admin User	admin.smoke.1788554761@example.com	$2a$10$90hj7dVJtLIaoDPUfreMnu0gy5z8kJg3Ywujpr8lET.Kq0csKQ4om	ADMIN	ACTIVE	2026-09-04 20:46:01.262503	2026-09-04 20:46:01.262523
9c9b580e-e7ad-474a-a842-3a3f21069dd0	admin User	admin.smoke.1788554800@example.com	$2a$10$JRnyGXFC.ED.03NIvMxv0OKnRMtOXzSyx3PAozYpoHVD9tMR2/4yG	ADMIN	ACTIVE	2026-09-04 20:46:40.87953	2026-09-04 20:46:40.879551
51f8151a-255b-4c48-8362-e260546cf518	admin User	admin.smoke.1788554818@example.com	$2a$10$C68kbSlWIJHJkrM3Klh2Y.a8LDXFvlFUc1A4x0gy.gXtxGI0NRNRO	ADMIN	ACTIVE	2026-09-04 20:46:59.040129	2026-09-04 20:46:59.04015
b8186312-36ff-46c2-b4c5-cd7f2e82f292	instructor User	instructor.smoke.1788554819@example.com	$2a$10$uCSlm68dyAkmd2Fa7LUOreoyYoh5c7Ul86cjavFW1OSURAG4YH3uW	STUDENT	ACTIVE	2026-09-04 20:46:59.316413	2026-09-04 20:46:59.316436
92d93d54-ec68-4ecb-9589-5f4f1ea53522	admin User	admin.smoke.1788554843@example.com	$2a$10$DYL3hRCqYn3fUUD3vSC3J.xY0KfxFlqhJmB1hZhDyGe45srTGJUVe	ADMIN	ACTIVE	2026-09-04 20:47:23.419573	2026-09-04 20:47:23.4196
82aa84fa-dfa5-4f86-aa2b-705c75b34d72	instructor User	instructor.smoke.1788554843@example.com	$2a$10$6ZXtBRr0qn3LXnL1zguGsu5zH5sb6iW7/j9JUkofRb1Fa6UW2iFMS	STUDENT	ACTIVE	2026-09-04 20:47:23.699246	2026-09-04 20:47:23.699258
340f08b1-e188-400a-b423-246762f79ec7	admin User	admin.smoke.1788554867@example.com	$2a$10$scco0haRmHJQft4QwaLNYu.xPyIaWkSHQb7S.gEUNx33a47P1eKFa	ADMIN	ACTIVE	2026-09-04 20:47:47.399763	2026-09-04 20:47:47.399775
463d747a-41c1-49ee-bf82-c9ef9e706867	instructor User	instructor.smoke.1788554867@example.com	$2a$10$RbToOn4nlrY1Wbvh9BBmPe7C639LxTdL3vmirADVgqEpsIR5CbGSm	INSTRUCTOR	ACTIVE	2026-09-04 20:47:47.668897	2026-09-04 20:47:47.668909
25915975-110d-4798-9ec3-2f47c0f96ff4	studentA User	studentA.smoke.1788554901@example.com	$2a$10$gZcKhAXYVHxyWNl4dH0cTO78.uT4wE6ipsXV2SF5V5U33WqWfAecG	STUDENT	ACTIVE	2026-09-04 20:48:21.745622	2026-09-04 20:48:21.745637
152bbb33-8da7-4617-8803-a6f77c33a3a1	admin User	admin.smoke.1788554900@example.com	$2a$10$uzvtmRz7Mrs5sl42jNCf1.3lsdm3ZnoAZL0P1Z1H5jHDxO0x9Wt7m	ADMIN	ACTIVE	2026-09-04 20:48:20.838805	2026-09-04 20:48:20.83883
10b2432f-15e9-49ea-b504-46da2b6c6c2d	instructor User	instructor.smoke.1788554901@example.com	$2a$10$JyWR/uGImxRSjqgs4hrNA.BAEINO3wvgnAeUofMf7Xa3x1VM6zDbS	INSTRUCTOR	ACTIVE	2026-09-04 20:48:21.102353	2026-09-04 20:48:21.102366
b18888ba-9c3f-4675-b822-aeff86291c15	studentB User	studentB.smoke.1788554901@example.com	$2a$10$FVss3pwM6idfEmHyvLbrG.E6pP3VRpRYrMLbAxwNBBKFVrmbCkdF6	STUDENT	ACTIVE	2026-09-04 20:48:21.88853	2026-09-04 20:48:21.888543
c8a60b2f-ae18-4654-b79c-60bdf8ad22df	Admin Smoke	admin.smoke.1788553273f54c@example.com	$2a$10$wsfEUtgC9bx.OxT3sWCiN.IhvN8CIS7JjIsO8IsmycfbfSsl.RnRC	ADMIN	ACTIVE	2026-09-04 20:21:14.246175	2026-09-04 20:21:14.246217
242b12bb-cc5c-444c-94c1-b5f5c1ae82f4	Inst Smoke	inst.smoke.17885532743c56@example.com	$2a$10$v/ThTW7sq1ZkOeHQI10pKubxCPZn8aFZ8k7gC10DOHjLSMTo6aRS6	INSTRUCTOR	ACTIVE	2026-09-04 20:21:14.743504	2026-09-04 20:21:14.743529
7da967ed-7a15-49f3-bbbb-a2f287bbda77	Studenta Smoke	studentA.smoke.1788553275627d@example.com	$2a$10$iphrbpLGdR1JcgsoaVAgv.GzkHJv3T0TwV8rd1LLxVzdPz8obBaNq	STUDENT	ACTIVE	2026-09-04 20:21:15.417739	2026-09-04 20:21:15.417759
71ce74d9-d42f-46f4-b4b9-6ad3a6431a4f	Studentb Smoke	studentB.smoke.178855327511aa@example.com	$2a$10$Jan7we4UKmn.xJFRtK/8Ee9UV17vqRm8d1dPwQPSY1lmt1MVWUVQW	STUDENT	ACTIVE	2026-09-04 20:21:15.567589	2026-09-04 20:21:15.567611
d113a8f4-89c0-4049-b689-df46f193259a	admin User	admin.smoke.1788554932@example.com	$2a$10$7BAXRF0bh/qev9hwnQY40.Ejj2uCTAuokHouvTLsFg5EBwdDTS6g.	ADMIN	ACTIVE	2026-09-04 20:48:52.116593	2026-09-04 20:48:52.11661
babbf64d-8de4-49ec-8747-5ad84085e624	instructor User	instructor.smoke.1788554932@example.com	$2a$10$Z8l/raMdOpdDYkH12S7a7.DpKLyNpt6UxWkF170y3dFXZ7rQVurri	INSTRUCTOR	ACTIVE	2026-09-04 20:48:52.414546	2026-09-04 20:48:52.414601
d8df887b-f670-4853-b4bb-0f14dbb9de01	studentA User	studentA.smoke.1788554932@example.com	$2a$10$bN1rw.qIYN.PDkPnLt3Az.cPp5xGlHitcZ1zJLyI0hifqodjx3/2a	STUDENT	ACTIVE	2026-09-04 20:48:53.052798	2026-09-04 20:48:53.052813
4fe218ec-3dec-478b-a71e-11a8e8325dfa	studentB User	studentB.smoke.1788554933@example.com	$2a$10$HBCt84w7E/zrwHt9umCIB.5qFm3avENam8NAIueFvKiHU5w6CA.jy	STUDENT	ACTIVE	2026-09-04 20:48:53.198856	2026-09-04 20:48:53.198912
5f4523f7-1360-4af6-9aa6-886c6c5dcb00	admin User	admin.smoke.1788554964@example.com	$2a$10$PZod0l4janEWFWUvdJc8q.Kz0BJ0goiw6TspOv92EOhEnvZOjtUBS	ADMIN	ACTIVE	2026-09-04 20:49:24.787954	2026-09-04 20:49:24.787967
c25df928-a86b-4926-94ce-1bc625a7d6a3	instructor User	instructor.smoke.1788554964@example.com	$2a$10$Bc1wwBY05ELJFdOAIiFBhOHfRn8.b2i7xEfpdsQEGszYFKSPeErEm	INSTRUCTOR	ACTIVE	2026-09-04 20:49:25.063482	2026-09-04 20:49:25.063495
ae39afb5-1419-4864-b308-380ca4bc0318	studentA User	studentA.smoke.1788554965@example.com	$2a$10$rFt4LThmDQQ9LVcMd3A1QOImGn0.TGADSey8rcwTngL22mD029NCy	STUDENT	ACTIVE	2026-09-04 20:49:25.662505	2026-09-04 20:49:25.66252
4e3a57d6-b3d7-4947-b6e8-3a85932c4ebe	studentB User	studentB.smoke.1788554965@example.com	$2a$10$6skQC2Cskx7P58J/LTvZXevs194kpFUkLA4hLcbC7taXR0X5XnqvW	STUDENT	ACTIVE	2026-09-04 20:49:25.802691	2026-09-04 20:49:25.802704
850e1ad4-1f5f-4a18-add0-2c0bb48755c3	admin User	admin.smoke.1788555021@example.com	$2a$10$aA3FnMg5tKUvKrh/yxo7xOgGbN8ggIh0tlwSBdqxa.2ijubVkhHAa	ADMIN	ACTIVE	2026-09-04 20:50:21.956524	2026-09-04 20:50:21.956538
983b88b9-c974-47d3-8716-d0d1bd63e22a	instructor User	instructor.smoke.1788555022@example.com	$2a$10$PupguE3X.0/UM8cTeCXIi.a1JwEwMjovfrROnZxRTs7wYzuhROdfa	INSTRUCTOR	ACTIVE	2026-09-04 20:50:22.22758	2026-09-04 20:50:22.227597
e028eff7-cace-4cd9-8c58-66f99765c577	studentA User	studentA.smoke.1788555022@example.com	$2a$10$z7UxYH2/l9wWriueJ8NBaeKq.kxGPRtfw5GG5fszhZft0t/g4g8aK	STUDENT	ACTIVE	2026-09-04 20:50:22.842396	2026-09-04 20:50:22.84241
c219f647-e599-4c9d-b025-28c3359d1b16	studentB User	studentB.smoke.1788555022@example.com	$2a$10$nqaC7s5UZdkkiMAUT6XuGO1FNkAyIc4li911wFzVJ2TeyShPu0lOm	STUDENT	ACTIVE	2026-09-04 20:50:22.984887	2026-09-04 20:50:22.984914
690cb5c0-be3f-40c7-a982-53433c4c55ac	admin User	admin.smoke.1788555130@example.com	$2a$10$1QfjmHVx5mRlgLAYrxu3vO2luaxGzD8DzjL/sXlBX3DT/o3gLqnRW	ADMIN	ACTIVE	2026-09-04 20:52:10.53002	2026-09-04 20:52:10.530054
311bf085-1810-4b02-bd11-967cb3d5c229	instructor User	instructor.smoke.1788555130@example.com	$2a$10$EaVfwD1HgPXw4Eck3jOqjuQONlJmIRzuWslK2C9Nwc.1SKKqVmUne	INSTRUCTOR	ACTIVE	2026-09-04 20:52:10.983099	2026-09-04 20:52:10.983125
4a415657-c567-40e9-b6e1-f72830513abf	studentA User	studentA.smoke.1788555131@example.com	$2a$10$a.SlWygWfNjmwH4XcM2avOKPxW6AH21t/QGf/K4luftDydX0fQY66	STUDENT	ACTIVE	2026-09-04 20:52:11.855083	2026-09-04 20:52:11.855104
fac2dfaf-9262-42df-b9f8-7e0e5e7d49c4	studentB User	studentB.smoke.1788555131@example.com	$2a$10$08UuGMAgBsZepQwyv/1yQe3LqhFMAYYVpzLw3vDajQKSRjYs.OUxe	STUDENT	ACTIVE	2026-09-04 20:52:12.00014	2026-09-04 20:52:12.000162
0eb46edf-7737-469a-b7e3-fc6db3d28462	admin User	admin.smoke.1788555148@example.com	$2a$10$1OFLLKmFqt3pU93XOvkjXu0sCzwEnhO7/HBNppLRs8rE8P4w93D/O	ADMIN	ACTIVE	2026-09-04 20:52:28.37975	2026-09-04 20:52:28.37977
81d7d496-489b-4736-bfcc-1b2c0775dced	instructor User	instructor.smoke.1788555148@example.com	$2a$10$l6H.F3EHxfolXyZuDRyIX.TFkIzav2pD6V.KG0Ri3WxptYM4TujV2	INSTRUCTOR	ACTIVE	2026-09-04 20:52:28.697322	2026-09-04 20:52:28.697342
18ee7bfd-04fc-40af-9e4b-a323c9107f6d	studentA User	studentA.smoke.1788555149@example.com	$2a$10$R11CIqshYjTk9keADjq8Dej7BqNsD9nZOS7wWydE68z7oEtLQM3Ue	STUDENT	ACTIVE	2026-09-04 20:52:29.372578	2026-09-04 20:52:29.372596
f06e8e85-606d-4f3c-9620-d0f7b0d0fb56	studentB User	studentB.smoke.1788555149@example.com	$2a$10$WIF.56scmQfIFZxgjr6v9O3.vLiKJ/RV0Xt6S0QsesUTn2mrVU.N6	STUDENT	ACTIVE	2026-09-04 20:52:29.519015	2026-09-04 20:52:29.519027
5ff327d8-0bb0-4535-a1a9-0a85abe00c0b	admin User	admin.smoke.1788555164@example.com	$2a$10$z8r2kkd/pXYIwX3z7Q6ucOE.HMsO450bKn68m0QNQWGw/UXht7taS	ADMIN	ACTIVE	2026-09-04 20:52:44.333447	2026-09-04 20:52:44.333468
51211ee5-b1fb-4b6a-b13f-851d1b4e41d8	instructor User	instructor.smoke.1788555164@example.com	$2a$10$Zerv/eJOSqBRZkLA5Pjpe.oWS7wsA1MIu6jhlVYN9d.TQLxd9hZNy	INSTRUCTOR	ACTIVE	2026-09-04 20:52:44.599312	2026-09-04 20:52:44.599325
6778c151-a472-4a01-955b-9993c45c7abc	studentA User	studentA.smoke.1788555165@example.com	$2a$10$MSkqmKgek/lc9BLrPk/gg.a3UlndbgvVfqRIAOGK3FQq.Wbo1Hhdq	STUDENT	ACTIVE	2026-09-04 20:52:45.228012	2026-09-04 20:52:45.228027
36ca528a-dcb3-482a-bc35-adcd98f9e4be	studentB User	studentB.smoke.1788555165@example.com	$2a$10$dy80UkYnoXy8Xky5C2f/7eMFUh4CfvnH.mhZFMKbN.DVaH5/tml2m	STUDENT	ACTIVE	2026-09-04 20:52:45.371026	2026-09-04 20:52:45.37104
ce96acdb-57cd-4d22-8cd8-b938e2065fd9	John Doe	instructor1@example.com	$2a$10$LBoXGhATNjhHV3AmskJjAev0dfRaSZzAbLMAnDDL22z/S3zbSrGo2	INSTRUCTOR	ACTIVE	2026-09-05 05:32:50.04413	2026-09-05 05:32:50.044171
a52467b0-5979-4392-8b61-784e7b690ec9	Admin	admin@test.com	$2a$10$TNsdvKV/aatwkIbFcBmPrey2Vxr6aIkU6C5Dzrc1Q7IQlPGxIb4kK	ADMIN	ACTIVE	2026-09-05 05:36:18.46405	2026-09-05 05:36:18.464093
c78237c1-3086-467e-bb04-dbaef8417345	Stu A	stuA_a334484c-9727-4ad5-b8b2-c5e276093721@test.com	$2a$10$N/MWxIVqIvGO9e64BTlxN.LBXwJjIbx2KnnmnxVWrBiotNIQX8N.u	STUDENT	ACTIVE	2026-09-08 22:53:52.664674	2026-09-08 22:53:52.664694
b3e3c445-6a76-45a7-ae72-55d400b15404	Stu B	stuB_96d3deaf-01ce-4509-b31f-cea03eb18780@test.com	$2a$10$rcoIF7cQt6JyyJyxvPRAQOINhyB58Tzdn7XoUV3M0YOO.4JnZVBZ2	STUDENT	ACTIVE	2026-09-08 22:53:52.907805	2026-09-08 22:53:52.90782
dd0998f6-f3fd-418d-9ff3-22f131b30b29	Test Instructor	instB_7357cdb1-1609-4b44-9fa9-b779f5194ab9@test.com	$2a$10$BdeRUVPQhPlUvSebQI1yD.1A3e/o9yPgeoPj7FbXCMZp8p6qjTole	INSTRUCTOR	ACTIVE	2026-09-08 22:53:53.139575	2026-09-08 22:53:53.139592
84031866-a057-4f51-a92f-f2a165978690	Test Instructor	inst_ccbb859a-2a08-4c3f-aa53-4792ce5b6466@test.com	$2a$10$hBX7GyoLml5Tz6.2uzh3vOfU88NU6nJkB5gWxM5mMitPq6gKbNPBC	INSTRUCTOR	ACTIVE	2026-09-08 22:55:10.804642	2026-09-08 22:55:10.804687
2e405854-6efe-4af0-bf76-25e9a30b16aa	Stu A	stuA_d4f41202-6a18-460e-bec0-eb02f9fcdc73@test.com	$2a$10$8J42mo0CCHbg4dpComRTpeW5YFN8T5za1Od0H8j6XSrbztEd837wa	STUDENT	ACTIVE	2026-09-08 22:55:11.503127	2026-09-08 22:55:11.503154
3c32d762-c086-4cba-9c41-adbb29b2ac6a	Stu B	stuB_0c514b92-4153-4e35-b4ad-3c48f5c011ed@test.com	$2a$10$Z67YMua9e15/jpcyFy2gzuIP0Q77PpUC.9IGjODmZ97jI8vGI42Hi	STUDENT	ACTIVE	2026-09-08 22:55:11.750069	2026-09-08 22:55:11.750099
bcdb8387-1653-4eee-9dbe-1c908396ea1d	Test Instructor	instB_3093b3b0-d8d7-48ee-a49d-24ca5e53d1e4@test.com	$2a$10$GkchvuOWxulXsyDxWkXYXOQWMZi1eXT0r2IXv5rKlxlZtkUj.D0/m	INSTRUCTOR	ACTIVE	2026-09-08 22:55:11.992542	2026-09-08 22:55:11.992606
fc00bf2e-60c8-4a2c-a925-082be0a320fe	Test Instructor	inst_e94aefd6-e470-42a2-844e-11bd8aa27c9e@test.com	$2a$10$4A/0T1P7KWrW4aff3QXN9umaRSfTSWBGk8sBFax0QEQaT2nwYjY1C	INSTRUCTOR	ACTIVE	2026-09-08 22:59:05.702024	2026-09-08 22:59:05.702084
9e7f80de-8073-4040-9dd2-deaaf5b3ea85	Stu A	stuA_e075dac9-fab2-43cd-ad7a-890c553a0648@test.com	$2a$10$0M76NRPVpgMGR6rFTSJrMO5txEgadAs9AMnQM8.UsX9Gg29WENNl2	STUDENT	ACTIVE	2026-09-08 22:59:06.302167	2026-09-08 22:59:06.302256
2bdd28e6-e01b-446b-afeb-e13c006eae6b	Stu B	stuB_5781bd46-e728-4fd4-bf6c-995a72332469@test.com	$2a$10$eoywru.WtfTfdZr4586sCOA4j/sPYxjdvwQw7cjqq6EvtWFYTxQ/W	STUDENT	ACTIVE	2026-09-08 22:59:06.645197	2026-09-08 22:59:06.645233
311d4464-9a0b-4b11-b3ee-8f7f0b6a4cea	Test Instructor	instB_1b73176e-9545-41dd-960c-f294311533c5@test.com	$2a$10$i9yUv4CmtUFF36cNCAEys.UgiS/BGpe7SHYIk9qgk9CIXO9PLzoBy	INSTRUCTOR	ACTIVE	2026-09-08 22:59:06.970794	2026-09-08 22:59:06.970836
e9007748-3c5c-49df-9151-44574aaab09c	admin User	admin.smoke.1788591050@example.com	$2a$10$uPOPtEI.KhHZV3lTLz7jVORFFa1LO5A/Tqnim.LUVCI/AYs45nUue	ADMIN	ACTIVE	2026-09-05 06:50:50.46294	2026-09-05 06:50:50.462984
7fc74edc-49ee-46c8-8d74-13e6ec721de3	instructor User	instructor.smoke.1788591050@example.com	$2a$10$YMw0d55vzfppLvs0Fqcpp.rACy4s4VGadOAE4t2KaZ.wBTlsmqTl6	INSTRUCTOR	ACTIVE	2026-09-05 06:50:50.848522	2026-09-05 06:50:50.848542
4be0a18d-fdb3-42dd-a6f9-decad439157c	admin User	admin.smoke.1788591145@example.com	$2a$10$fPLZtvyhd3gwL2PmYjmKDuf5obaKvkScacVCzd.HOsaPxdeOFBiuq	ADMIN	ACTIVE	2026-09-05 06:52:25.168542	2026-09-05 06:52:25.168561
1f0ff986-1962-4136-b81f-4b1d8bf5657b	instructor User	instructor.smoke.1788591145@example.com	$2a$10$ucMpIne80Ys4UHAEqXbjKOwUGlQRK9X1fc1WJON4yUFbuuFe3L3Ji	INSTRUCTOR	ACTIVE	2026-09-05 06:52:25.499913	2026-09-05 06:52:25.499936
7098270e-662e-43b1-a80e-09415339bdf8	studentA User	studentA.smoke.1788591146@example.com	$2a$10$AH9JgBfpFYrKnG50R9XSpeXdTP/QVbSCC8Bq797kGOkT9Or2F5Qee	STUDENT	ACTIVE	2026-09-05 06:52:26.437435	2026-09-05 06:52:26.437456
3b108fb7-7160-47d0-b433-c6723efcabd5	studentB User	studentB.smoke.1788591146@example.com	$2a$10$N5Qj2NozapsyToD7tCE7WOhEHT9GMP/g3vyL.ENVx7vh.NNPeNlzC	STUDENT	ACTIVE	2026-09-05 06:52:26.612528	2026-09-05 06:52:26.612549
1e3ece52-93ea-47a6-aee7-77b62a3c8a46	admin User	admin.smoke.1788591176@example.com	$2a$10$l1efiSayR9jHESssu7/Lt.awzk.hQBEDFOvuOdzCJ2YycXK9TM7oq	ADMIN	ACTIVE	2026-09-05 06:52:56.185683	2026-09-05 06:52:56.1857
945d0bf5-cffc-40e3-8367-d5abdb93f6b0	instructor User	instructor.smoke.1788591176@example.com	$2a$10$6mr7UBB41uaeY8mJS3/DmuNt5jMw7fJN4uP7mQQJzqCOHYSBXd/TO	INSTRUCTOR	ACTIVE	2026-09-05 06:52:56.49703	2026-09-05 06:52:56.497043
7cffd4de-3cd2-49d7-abed-1ff694da1e85	studentA User	studentA.smoke.1788591177@example.com	$2a$10$clAhH4eyg3Wbk3hb8ehB2OUgh6.9.dfBm53fQq8SBXq5E1KxNSPDC	STUDENT	ACTIVE	2026-09-05 06:52:57.417154	2026-09-05 06:52:57.417169
d8036eaf-63c1-4f14-bcbf-1e886b4cfb5f	studentB User	studentB.smoke.1788591177@example.com	$2a$10$25JyDxyfyry82uN3XvBiPelXpHqeIOfaErZsDcfvQEoQhiBVM64Ou	STUDENT	ACTIVE	2026-09-05 06:52:57.605748	2026-09-05 06:52:57.605762
b2ee6294-ef04-4961-972a-402d35dc5de8	admin User	admin.smoke.1788591327@example.com	$2a$10$Cd9FF3dkO9hj8gxGIj.OO.xiBx/9yfagvj2GkFtgqL7FyNDKMb1r6	ADMIN	ACTIVE	2026-09-05 06:55:28.323436	2026-09-05 06:55:28.323472
a8baa6fa-f2d4-4281-b400-032e58b08d26	instructor User	instructor.smoke.1788591328@example.com	$2a$10$m4Bhf2Aob5gSYNOza.hYKe2jaVajlsWHPI1GnEkf18mqGZa0zu1Iu	INSTRUCTOR	ACTIVE	2026-09-05 06:55:28.899163	2026-09-05 06:55:28.899186
1aa71ecd-0624-4fb0-a3a2-befff2006a59	studentA User	studentA.smoke.1788591329@example.com	$2a$10$QeCBXMvEI96MdaH2oOLpGeVnz3Tzp83329sURAqjb030l8Axjb9dK	STUDENT	ACTIVE	2026-09-05 06:55:29.960134	2026-09-05 06:55:29.960155
ff6d444d-6475-49b0-a447-ef196710d9e4	studentB User	studentB.smoke.1788591330@example.com	$2a$10$5Y59v9uEFNLhgZZhyKCZMefuOvQLc205ZxgUMdznwzn16rl8kd50S	STUDENT	ACTIVE	2026-09-05 06:55:30.137669	2026-09-05 06:55:30.137713
beb0639d-3908-4f37-81d4-bc0eb7607f38	admin User	admin.smoke.1788609897@example.com	$2a$10$AoDZPB18OK.luH7vPWcosuaTNTDqc/2UjUtIECC9tdR8Uw1ocpsne	ADMIN	ACTIVE	2026-09-05 12:04:57.846158	2026-09-05 12:04:57.846199
f4642dec-b700-419c-bd48-f07b37b6d7a6	instructor User	instructor.smoke.1788609898@example.com	$2a$10$gA09531oRsU7dMOetqPzM.gpG2e7CSCP9298e8A2A4y8SZCeKsADq	INSTRUCTOR	ACTIVE	2026-09-05 12:04:58.449415	2026-09-05 12:04:58.449438
cb39f934-c1d9-4790-9ef5-53e42c138548	studentA User	studentA.smoke.1788609899@example.com	$2a$10$OKn7OdafNEgjrcDNkJXseu2pIGH3l3Vb894d9vLyEVt5dvB74NaMW	STUDENT	ACTIVE	2026-09-05 12:04:59.57314	2026-09-05 12:04:59.57316
980054f2-062e-4ff8-90b6-fbdf05564878	studentB User	studentB.smoke.1788609899@example.com	$2a$10$GSoYclYbk1oq6d/M76gpyepF3YSuBVoagxkZI8dZrVBG3nOCq3RMW	STUDENT	ACTIVE	2026-09-05 12:04:59.748558	2026-09-05 12:04:59.748604
cd3f1c6d-6d1f-43ab-894b-55c4f55dc23c	admin User	admin.smoke.1788609938@example.com	$2a$10$KdGNcS7x8RdLAm3IPhWunOYSrJsrUOnbR4SaBjaWMvojudZd6z02i	ADMIN	ACTIVE	2026-09-05 12:05:38.787895	2026-09-05 12:05:38.787914
fdd28450-5d3e-418e-9eed-3c601b93bf6a	instructor User	instructor.smoke.1788609939@example.com	$2a$10$c3YiBhG.9UInMIHD3VuwMe7IObo/Rf02H504NvxYmaQsLZSpol1PG	INSTRUCTOR	ACTIVE	2026-09-05 12:05:39.078912	2026-09-05 12:05:39.078929
b2f34101-1078-44fa-bd60-ab41b8d60782	studentA User	studentA.smoke.1788609939@example.com	$2a$10$8d.94Ido1cLqqEAKBzOIm.wlE6supfyk2vXlXxfxASHXns3pz95Zy	STUDENT	ACTIVE	2026-09-05 12:05:39.877387	2026-09-05 12:05:39.877401
dde5c8ad-a98f-4228-9501-d38aaec3a218	studentB User	studentB.smoke.1788609939@example.com	$2a$10$aJRQf0zyGfWIqE.OnsKkkupW4erZhk0YEKPGkHKofmPaum1q2H8Si	STUDENT	ACTIVE	2026-09-05 12:05:40.058069	2026-09-05 12:05:40.058084
9f444634-1e8a-434b-9aca-e2b5e8054ad8	admin User	admin.smoke.1788609982@example.com	$2a$10$S87X2Cr3je1YuHDDccOVR.bXFzvzDujeUimNxQMVtzoKHzs554I4C	ADMIN	ACTIVE	2026-09-05 12:06:22.160617	2026-09-05 12:06:22.160633
ec21ed5f-c1eb-4487-b706-fc30b5be937b	instructor User	instructor.smoke.1788609982@example.com	$2a$10$ILX/rvI89/iwFhAFdaHtT.r4cEkjsAdesXy.j1we1dSGqkItdXLY.	INSTRUCTOR	ACTIVE	2026-09-05 12:06:22.50208	2026-09-05 12:06:22.502096
380cc8eb-5dc7-49d8-ab91-118611bac1be	studentA User	studentA.smoke.1788609983@example.com	$2a$10$e7Yd5rvmHoi1BWSmLaJ9ne15cQoov5UtkV.Lq8OaE2/ZTrjA2xBSe	STUDENT	ACTIVE	2026-09-05 12:06:23.311216	2026-09-05 12:06:23.311231
d97cd9a0-7f89-42e7-80d6-f2537417341d	studentB User	studentB.smoke.1788609983@example.com	$2a$10$rbdw/txXfQXKXVWiLyxiWOIJZNiObAI8W1cYghv.vyrLaGzUilHsW	STUDENT	ACTIVE	2026-09-05 12:06:23.486144	2026-09-05 12:06:23.486174
548c8649-9d28-4adb-ac9d-e913144fd9ca	student 1	student1@example.com	$2a$10$LkI88nLy8242JwFszAYJh.XmFBsUZi.MmT9nNfG/l7cDYDlAE6s3a	STUDENT	ACTIVE	2026-09-08 20:14:07.549216	2026-09-08 20:14:07.549284
\.


--
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.courses (id, instructor_id, title, description, category, difficulty, price, thumbnail_url, publish_status, rating, student_count, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: sections; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.sections (id, course_id, title, description, display_order, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: lessons; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.lessons (id, section_id, title, description, lesson_type, display_order, created_at, updated_at, duration_seconds) FROM stdin;
\.


--
-- Data for Name: assignments; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.assignments (id, lesson_id, title, instructions, max_score, due_at, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: assignment_submissions; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.assignment_submissions (id, assignment_id, user_id, submission_text, status, score, feedback, graded_by, graded_at, submitted_at, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: enrollments; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.enrollments (id, user_id, course_id, status, enrolled_at, updated_at) FROM stdin;
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	baseline schema	SQL	V1__baseline_schema.sql	1162166458	edtech_user	2026-09-04 16:31:43.868391	23	t
2	2	create users table	SQL	V2__create_users_table.sql	944446943	edtech_user	2026-09-04 16:31:43.951051	21	t
3	3	create instructor profiles	SQL	V3__create_instructor_profiles.sql	1533011164	edtech_user	2026-09-04 16:31:44.002467	29	t
4	4	create courses table	SQL	V4__create_courses_table.sql	154002772	edtech_user	2026-09-04 16:31:44.059392	24	t
5	5	create sections table	SQL	V5__create_sections_table.sql	832450533	edtech_user	2026-09-04 16:31:44.110208	22	t
6	6	create lessons table	SQL	V6__create_lessons_table.sql	-1108398924	edtech_user	2026-09-04 16:31:44.154991	25	t
7	7	add course discovery indexes	SQL	V7__add_course_discovery_indexes.sql	495039108	edtech_user	2026-09-04 16:31:44.200337	23	t
8	8	create enrollments table	SQL	V8__create_enrollments_table.sql	-170921126	edtech_user	2026-09-04 16:31:44.245834	31	t
9	9	create lesson progress	SQL	V9__create_lesson_progress.sql	-1736275599	edtech_user	2026-09-05 01:15:54.914012	101	t
10	10	create quizzes	SQL	V10__create_quizzes.sql	2083133869	edtech_user	2026-09-05 02:12:42.636694	157	t
11	11	create assignments	SQL	V11__create_assignments.sql	-1612507475	edtech_user	2026-09-05 17:23:03.968981	119	t
12	12	create payments	SQL	V12__create_payments.sql	431882886	edtech_user	2026-09-05 18:26:27.552913	89	t
13	13	create media assets	SQL	V13__create_media_assets.sql	1518464461	edtech_user	2026-09-09 03:14:34.414413	87	t
\.


--
-- Data for Name: instructor_profiles; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.instructor_profiles (id, user_id, bio, expertise, verification_status, created_at, updated_at) FROM stdin;
464a20ac-d557-4fb9-8951-21b96bbd3523	a7534d1f-47ea-4376-ba52-a4963360a8ac	Smoke test instructor for the EdTech platform	Java, Spring Boot, PostgreSQL	VERIFIED	2026-09-04 18:10:21.309522+00	2026-09-04 18:10:22.256897+00
8181eac0-ffd5-4736-a842-de092bafb50d	242b12bb-cc5c-444c-94c1-b5f5c1ae82f4	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:21:14.886186+00	2026-09-04 20:21:14.944953+00
d6785ae4-dc58-4874-870c-b803f4ba8860	fac32528-8b91-4aa4-a6ac-87e3d6bbdb29	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:44:10.728717+00	2026-09-04 20:44:10.778604+00
da9fc37f-5534-4d68-84b0-5e22dbaeb28e	ce96acdb-57cd-4d22-8cd8-b938e2065fd9	Senior Software Engineer	Java, Spring Boot, Microservices, PostgreSQL	VERIFIED	2026-09-05 05:38:29.901564+00	2026-09-05 05:45:13.990542+00
bb3c1b42-fff8-43b0-af52-95c7bf1d4199	23e76988-9506-4943-8560-30ebd57b648e	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:13:00.593284+00	2026-09-04 20:13:00.63692+00
65ba8ae5-6a19-4777-9d81-f1c08c4eb5c1	dddf701e-772c-4367-a5db-70bf01ab408c	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:13:29.87663+00	2026-09-04 20:13:29.90727+00
2eecff56-8f63-4c4c-9db4-3671840fa5c6	0771c509-7296-4f5d-b62f-db8c4efa313d	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:14:02.789661+00	2026-09-04 20:14:02.827525+00
9b245d40-d7ce-4a4d-9533-79fa3b943810	f4642dec-b700-419c-bd48-f07b37b6d7a6	Expert	Java, Spring	VERIFIED	2026-09-05 12:04:58.869953+00	2026-09-05 12:04:58.962077+00
466039fd-d068-4470-b16c-665a15df0a8e	a7b91af0-1da2-4116-b457-d848f6220f0c	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:15:14.407303+00	2026-09-04 20:15:14.435799+00
f41eb5cd-18f1-45ba-88d8-eb95bd2e1b4f	cd2a9bf4-6df5-4dd8-9b7b-a0f482d16376	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:16:24.530255+00	2026-09-04 20:16:24.559516+00
9c71b5d5-c444-4434-bc65-b97fd8d8f7a9	5b72b54a-a7de-433b-bd39-2e84074ec73b	I am a smoke tester	Testing	VERIFIED	2026-09-04 20:18:53.195475+00	2026-09-04 20:18:53.225395+00
c2e2f602-3da8-4696-87d2-a961260010e2	fdd28450-5d3e-418e-9eed-3c601b93bf6a	Expert	Java, Spring	VERIFIED	2026-09-05 12:05:39.436689+00	2026-09-05 12:05:39.474179+00
eca2bbb9-da6c-4558-8ffd-9932f7c37314	1f0ff986-1962-4136-b81f-4b1d8bf5657b	Expert	Java, Spring	VERIFIED	2026-09-05 06:52:25.801212+00	2026-09-05 06:52:25.840251+00
9e674489-f79e-4907-b5ec-16579397a0a9	945d0bf5-cffc-40e3-8367-d5abdb93f6b0	Expert	Java, Spring	VERIFIED	2026-09-05 06:52:56.811104+00	2026-09-05 06:52:56.947555+00
d30f21d5-a148-458c-ba54-cbfc5a4b3193	a8baa6fa-f2d4-4281-b400-032e58b08d26	Expert	Java, Spring	VERIFIED	2026-09-05 06:55:29.342551+00	2026-09-05 06:55:29.401863+00
137e96d2-ba0e-4c23-b6b5-446135f520a0	ec21ed5f-c1eb-4487-b706-fc30b5be937b	Expert	Java, Spring	VERIFIED	2026-09-05 12:06:22.837968+00	2026-09-05 12:06:22.879801+00
cbcada0a-7bc7-43e9-87b0-65cef1e62afc	fc00bf2e-60c8-4a2c-a925-082be0a320fe	Bio	Expertise	APPROVED	2026-09-08 22:59:06.006819+00	2026-09-08 22:59:06.006819+00
0d06c128-8a81-496a-824a-f5c98d6b2aec	311d4464-9a0b-4b11-b3ee-8f7f0b6a4cea	Bio	Expertise	APPROVED	2026-09-08 22:59:07.28074+00	2026-09-08 22:59:07.28074+00
\.


--
-- Data for Name: lesson_progress; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.lesson_progress (id, user_id, course_id, lesson_id, last_position_seconds, max_position_seconds, status, completed_at, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: media_assets; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.media_assets (id, lesson_id, original_file_name, storage_key, content_type, file_size, duration_seconds, processing_status, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: payments; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.payments (id, user_id, course_id, provider, checkout_session_id, payment_intent_id, amount, currency, status, created_at, updated_at, paid_at) FROM stdin;
\.


--
-- Data for Name: quizzes; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.quizzes (id, lesson_id, title, description, pass_score, attempts_allowed, time_limit_seconds, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: quiz_questions; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.quiz_questions (id, quiz_id, question_text, question_type, points, display_order, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: question_options; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.question_options (id, question_id, option_text, display_order, is_correct, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: quiz_attempts; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.quiz_attempts (id, quiz_id, user_id, attempt_number, status, score, percentage, passed, started_at, completed_at) FROM stdin;
\.


--
-- Data for Name: quiz_attempt_answers; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.quiz_attempt_answers (id, attempt_id, question_id, points_awarded, created_at) FROM stdin;
\.


--
-- Data for Name: quiz_attempt_answer_options; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.quiz_attempt_answer_options (answer_id, option_id) FROM stdin;
\.


--
-- Data for Name: stripe_events; Type: TABLE DATA; Schema: public; Owner: edtech_user
--

COPY public.stripe_events (event_id, type, created_at) FROM stdin;
\.


--
-- PostgreSQL database dump complete
--

\unrestrict 9rAFX0PopqAwuT0Gcz5lDjCHGwKoKPnyEQiHWlNKch9tECfyZ6YgidDs6DbV2ay

