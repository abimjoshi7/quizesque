-- Second editorial batch: 40 new questions per category (120 total).
-- The launch-v1-review pack remains untouched. This pack and its questions
-- stay unpublished until a reviewer approves the full batch.
-- Run in the Supabase SQL Editor with a trusted role after checking the links.

begin;

insert into public.question_packs (slug, title, description, version, locale, state)
values ('launch-v2-review', 'Launch Set v2',
        'Forty additional sourced questions per category to expand the rotation.',
        1, 'en-IN', 'draft')
on conflict (slug) do nothing;

with gk (
    seed_key, topic_slug, prompt, options, correct_index, explanation, source_url, difficulty, position
) as (values
    ('planet-count','gk.science','How many planets are officially counted in our solar system?','{Seven,Eight,Nine,Ten}',1,'The solar system has eight planets. Pluto is classified as a dwarf planet under the International Astronomical Union definition.','https://science.nasa.gov/solar-system/planets/','easy',1),
    ('dwarf-count','gk.science','How many dwarf planets does NASA list as officially recognized in our solar system?','{Three,Four,Five,Six}',2,'NASA lists five officially recognized dwarf planets: Ceres, Pluto, Haumea, Makemake, and Eris.','https://science.nasa.gov/solar-system/planets/','medium',2),
    ('mercury-nearest','gk.science','Which planet is closest to the Sun?','{Venus,Mercury,Earth,Mars}',1,'Mercury is the innermost planet and orbits closest to the Sun.','https://science.nasa.gov/solar-system/planets/','easy',3),
    ('mercury-smallest','gk.science','Which is the smallest planet in the solar system?','{Mars,Mercury,Venus,Earth}',1,'Mercury is the smallest planet in the solar system as well as the closest planet to the Sun.','https://science.nasa.gov/solar-system/planets/','easy',4),
    ('inner-planets','gk.science','Which group contains the four terrestrial planets, in order from the Sun?','{Mercury, Venus, Earth, Mars|Venus, Earth, Mars, Jupiter|Earth, Mars, Jupiter, Saturn|Mercury, Earth, Jupiter, Neptune}',0,'The inner terrestrial planets are Mercury, Venus, Earth, and Mars; they have solid surfaces.','https://science.nasa.gov/solar-system/planets/','medium',5),
    ('milky-way','gk.science','What kind of galaxy contains our solar system?','{Elliptical galaxy,Barred spiral galaxy,Irregular galaxy,Lenticular galaxy}',1,'Our solar system is in the Milky Way, which NASA describes as a barred spiral galaxy.','https://science.nasa.gov/solar-system/solar-system-facts/','medium',6),
    ('solar-formation','gk.science','About how long ago did the solar system form?','{460 million years,4.6 billion years,46 billion years,13.8 billion years}',1,'NASA describes the solar system as forming about 4.6 billion years ago from a dense cloud of gas and dust.','https://science.nasa.gov/solar-system/solar-system-facts/','medium',7),
    ('sun-matter','gk.science','Approximately what share of the solar system’s available matter did the Sun amass as it formed?','{About 10 percent,About 50 percent,More than 99 percent,Exactly 75 percent}',2,'As material gathered toward the center of the young solar system, the Sun amassed more than 99 percent of the available matter.','https://science.nasa.gov/solar-system/solar-system-facts/','hard',8),
    ('venus-moons','gk.science','Which pair of planets have no natural moons?','{Earth and Mars,Mercury and Venus,Jupiter and Saturn,Uranus and Neptune}',1,'NASA notes that Mercury and Venus are the only two planets in the solar system with no moons.','https://science.nasa.gov/solar-system/solar-system-facts/','easy',9),
    ('pluto-moons','gk.science','How many moons orbit Pluto?','{One,Three,Five,Seven}',2,'Pluto has five known moons, including Charon, which is large enough to make Pluto wobble as they orbit.','https://science.nasa.gov/solar-system/solar-system-facts/','medium',10),
    ('jupiter-largest','gk.science','Which planet is the largest in our solar system?','{Saturn,Jupiter,Neptune,Uranus}',1,'Jupiter is the largest planet; NASA notes that roughly 1,000 Earths could fit inside it if it were hollow.','https://science.nasa.gov/jupiter/jupiter-facts/','easy',11),
    ('jupiter-day','gk.science','Which planet has the shortest day in the solar system, at about 9.9 hours?','{Mercury,Jupiter,Earth,Neptune}',1,'Jupiter rotates once in about 9.9 hours, giving it the shortest day of any planet in the solar system.','https://science.nasa.gov/jupiter/jupiter-facts/','medium',12),
    ('red-spot','gk.science','The Great Red Spot is a huge storm on which planet?','{Mars,Jupiter,Saturn,Neptune}',1,'The Great Red Spot is a long-observed, enormous storm in Jupiter’s atmosphere.','https://science.nasa.gov/jupiter/jupiter-facts/','easy',13),
    ('jupiter-atmosphere','gk.science','Which two elements make up most of Jupiter’s atmosphere?','{Nitrogen and oxygen,Hydrogen and helium,Carbon and oxygen,Argon and neon}',1,'Jupiter’s atmosphere is composed mostly of hydrogen and helium, with clouds and trace compounds creating its bands.','https://science.nasa.gov/jupiter/jupiter-facts/','medium',14),
    ('ganymede','gk.science','Which moon is the largest in the solar system?','{Titan,Europa,Ganymede,The Moon}',2,'Ganymede, which orbits Jupiter, is the largest moon in the solar system and is even larger than Mercury.','https://science.nasa.gov/jupiter/jupiter-facts/','medium',15),
    ('outer-planets','gk.science','Which pair are the solar system’s ice giants?','{Jupiter and Saturn,Uranus and Neptune,Mercury and Venus,Earth and Mars}',1,'Uranus and Neptune are called ice giants; Jupiter and Saturn are classified as gas giants.','https://science.nasa.gov/solar-system/solar-system-facts/','medium',16),
    ('earth-known-life','gk.science','On which planet has life so far been confirmed?','{Earth only,Mars and Earth,Venus only,All terrestrial planets}',0,'NASA states that so far we know of life only on Earth, while scientists continue searching elsewhere.','https://science.nasa.gov/solar-system/solar-system-facts/','easy',17),
    ('asteroid-belt','gk.science','Between which two planets is the main asteroid belt located?','{Mercury and Venus,Earth and Mars,Mars and Jupiter,Saturn and Uranus}',2,'The main asteroid belt lies between Mars and Jupiter and contains material that did not combine into a planet.','https://science.nasa.gov/solar-system/solar-system-facts/','easy',18),
    ('mom-date','gk.history','In which year did India’s Mars Orbiter Mission launch?','{2008,2011,2013,2016}',2,'India launched the Mars Orbiter Mission, also called Mangalyaan, on 5 November 2013.','https://www.isro.gov.in/FAQ_MarsOrbiterMission.html','easy',19),
    ('mom-vehicle','gk.history','Which launch vehicle carried India’s Mars Orbiter Mission?','{PSLV-C25,GSLV-F12,LVM3-M4,PSLV-C11}',0,'ISRO launched the Mars Orbiter Mission aboard PSLV-C25 from Satish Dhawan Space Centre.','https://www.isro.gov.in/MarsOrbiterMissionSpacecraft.html','medium',20),
    ('mom-arrival','gk.history','On what date did India’s Mars Orbiter Mission enter Mars orbit?','{5 November 2013,24 September 2014,23 August 2023,6 January 2024}',1,'ISRO records that Mangalyaan reached Mars orbit on 24 September 2014, after launching in November 2013.','https://www.isro.gov.in/FAQ_MarsOrbiterMission.html','medium',21),
    ('mom-payloads','gk.science','How many scientific instruments did the Mars Orbiter Mission carry?','{Three,Four,Five,Seven}',2,'MOM carried five scientific instruments to study Martian surface features, mineralogy, and atmosphere.','https://www.isro.gov.in/MarsOrbiterMissionSpacecraft.html','medium',22),
    ('aditya-purpose','gk.science','What is Aditya-L1 primarily designed to study?','{The Sun,The Moon,Earth’s oceans,Saturn’s rings}',0,'Aditya-L1 is India’s first space-based observatory dedicated to studying the Sun and its outer atmosphere.','https://www.isro.gov.in/ISRO_EN/Aditya_L1.html','easy',23),
    ('aditya-distance','gk.science','About how far from Earth is the Sun–Earth L1 point where Aditya-L1 operates?','{150000 km|1.5 million km|15 million km|150 million km}',1,'The Sun–Earth L1 point is about 1.5 million kilometres from Earth, roughly one percent of the Sun–Earth distance.','https://www.isro.gov.in/Aditya_L1-MissionDetails.html','medium',24),
    ('aditya-launch','gk.history','When was Aditya-L1 launched?','{2 September 2023,6 January 2024,14 July 2023,1 January 2024}',0,'ISRO launched Aditya-L1 on 2 September 2023; it later entered its planned halo orbit around L1.','https://www.isro.gov.in/ISRO_EN/Aditya_L1.html','medium',25),
    ('ch1-water','gk.science','Which lunar mission reported evidence of water molecules on the Moon before Chandrayaan-3?','{Chandrayaan-1,Mangalyaan,AstroSat,Aditya-L1}',0,'ISRO lists hydroxyl and water molecules among Chandrayaan-1’s major scientific results.','https://www.isro.gov.in/ISRO_EN/Chandrayaan-1_science.html','medium',26),
    ('ch1-date','gk.history','In which year did Chandrayaan-1 launch?','{2003,2008,2013,2019}',1,'Chandrayaan-1, India’s first Moon mission, launched from Sriharikota on 22 October 2008.','https://www.isro.gov.in/ISRO_EN/Chandrayaan-1_science.html','easy',27),
    ('ch3-lander','gk.history','What was the name of Chandrayaan-3’s lunar lander?','{Pragyan,Vikram,Rohini,Aditya}',1,'Vikram was the Chandrayaan-3 lander; the rover it carried was named Pragyan.','https://www.isro.gov.in/Chandrayaan3.html','easy',28),
    ('ch3-rover','gk.history','What was the name of the rover deployed by Chandrayaan-3?','{Vikram,Pragyan,Aryabhata,Bhaskara}',1,'Pragyan was the rover that rolled onto the lunar surface after the Vikram lander’s successful touchdown.','https://www.isro.gov.in/Chandrayaan3.html','easy',29),
    ('ch3-date','gk.history','On which date did Chandrayaan-3’s lander successfully touch down on the Moon?','{14 July 2023,23 August 2023,2 September 2023,6 January 2024}',1,'ISRO reported the successful soft landing of Chandrayaan-3’s Vikram lander on 23 August 2023.','https://www.isro.gov.in/Chandrayaan3.html','medium',30),
    ('ch3-purpose','gk.science','Which capability was Chandrayaan-3 designed to demonstrate?','{Soft landing and rover mobility on the Moon,Returning samples from Mars,Orbiting the Sun,Launching a crew to the ISS}',0,'Chandrayaan-3’s goals included demonstrating a safe lunar soft landing and rover mobility on the Moon.','https://www.isro.gov.in/Chandrayaan3.html','medium',31),
    ('xposat-purpose','gk.science','What kind of radiation does XPoSat study?','{X-rays,Sound waves,Visible ocean light,Neutrinos from Earth’s core}',0,'XPoSat is an X-ray polarimetry mission designed to study polarization in emissions from astronomical sources.','https://www.isro.gov.in/XPoSat.html','medium',32),
    ('xposat-launch','gk.history','In which year was XPoSat launched?','{2019,2021,2023,2024}',3,'ISRO launched XPoSat on 1 January 2024 aboard PSLV-C58.','https://www.isro.gov.in/SpacecraftMissions.html','easy',33),
    ('un-languages-count','gk.world','How many official languages does the United Nations have?','{Four,Five,Six,Eight}',2,'The UN has six official languages: Arabic, Chinese, English, French, Russian, and Spanish.','https://www.un.org/en/node/121653','easy',34),
    ('un-language-arabic','gk.world','Which of these is an official language of the United Nations?','{Hindi,Arabic,Portuguese,Japanese}',1,'Arabic is one of the UN’s six official languages, alongside Chinese, English, French, Russian, and Spanish.','https://www.un.org/en/node/121653','easy',35),
    ('un-charter-sign','gk.history','In which city was the UN Charter signed in June 1945?','{London,New York,San Francisco,Geneva}',2,'The UN Charter was signed in San Francisco on 26 June 1945 before coming into force that October.','https://www.un.org/en/about-us/un-charter/','medium',36),
    ('un-charter-date','gk.history','On what date did the UN Charter come into force?','{26 June 1945,24 October 1945,10 December 1948,1 January 1950}',1,'The UN Charter came into force on 24 October 1945 after ratification by the required states.','https://www.un.org/en/about-us/un-charter/','medium',37),
    ('water-ocean-store','gk.science','Approximately what percentage of Earth’s water is held in the oceans?','{About 25 percent,About 50 percent,About 71 percent,About 96.5 percent}',3,'USGS estimates that oceans hold about 96.5 percent of all Earth’s water; much of the remainder is frozen or underground.','https://www.usgs.gov/water-science-school/science/how-much-water-there-earth','medium',38),
    ('water-fresh','gk.science','Where is most of Earth’s fresh water stored?','{Rivers and lakes,Ice caps and glaciers,The atmosphere,Soil near the surface}',1,'Most of Earth’s fresh water is locked in ice caps and glaciers, while a large share of the rest is groundwater.','https://www.usgs.gov/water-science-school/science/how-much-water-there-earth','medium',39),
    ('earth-oceans','gk.world','According to USGS, about what percentage of Earth’s surface is water-covered?','{29 percent,51 percent,71 percent,96.5 percent}',2,'About 71 percent of Earth’s surface is water-covered; this describes surface area, not the share of all water.','https://www.usgs.gov/water-science-school/science/how-much-water-there-earth','easy',40)
),
gk_rows as (
    select 'gk-v2-' || seed_key as seed_key, 'gk'::text as category_id, topic_slug,
           prompt,
           case when position in (5, 24) then string_to_array(trim(both '{}' from options), '|')
                else string_to_array(trim(both '{}' from options), ',') end as options,
           correct_index, explanation, source_url, 'A'::text as source_grade,
           difficulty, 'recall'::text as skill,
           array['launch-v2','general-knowledge']::text[] as tags, position
    from gk
),
word_seed (seed_key, term, definition, example, position) as (values
    ('abundant','abundant','existing in large quantities; more than enough','The garden had abundant sunlight throughout the summer.',41),
    ('arduous','arduous','needing a lot of effort and energy','The arduous hike climbed steep paths for most of the day.',42),
    ('coherent','coherent','clear, logical, and easy to understand','Her coherent summary connected each event in the right order.',43),
    ('concise','concise','giving information clearly in few words','A concise note explained the change without extra detail.',44),
    ('diligent','diligent','working carefully and with steady effort','The diligent student checked every calculation twice.',45),
    ('elated','elated','extremely happy and excited','The team felt elated after hearing the final result.',46),
    ('frugal','frugal','careful to avoid wasting money or resources','A frugal traveller planned meals to keep costs low.',47),
    ('impartial','impartial','not supporting one side more than another','An impartial judge listened to both arguments fairly.',48),
    ('inevitable','inevitable','certain to happen and impossible to avoid','With dark clouds overhead, a delay seemed inevitable.',49),
    ('innovate','innovate','to introduce new ideas, methods, or products','The studio had to innovate to make the game easier to learn.',50),
    ('novice','novice','a person new to an activity or subject','As a novice gardener, he began with easy herbs.',51),
    ('profound','profound','very great or serious; showing deep insight','The book had a profound effect on how she viewed history.',52),
    ('scarce','scarce','not available in large amounts; difficult to find','Clean water became scarce after several dry months.',53),
    ('serene','serene','calm, peaceful, and without disturbance','They watched the sunrise beside a serene lake.',54),
    ('reluctant','reluctant','not willing or eager to do something','She was reluctant to leave before hearing the results.',55),
    ('vigilant','vigilant','watching carefully for possible danger or trouble','The lifeguard stayed vigilant while swimmers were in the water.',56),
    ('endorse','endorse','to publicly support or approve something','The committee voted to endorse the revised safety plan.',57),
    ('hinder','hinder','to make progress difficult or slow','Heavy traffic can hinder emergency vehicles.',58),
    ('immense','immense','extremely large or great','The telescope revealed an immense cloud of gas and dust.',59),
    ('jovial','jovial','cheerful and friendly','Their jovial host welcomed every guest with a joke.',60),
    ('keen','keen','very interested, eager, or enthusiastic','Mira is keen to learn how the puzzle works.',61),
    ('lethargic','lethargic','having little energy or enthusiasm','After the long journey, the group felt lethargic.',62),
    ('mundane','mundane','ordinary and not interesting or unusual','The checklist turned a mundane task into a quick routine.',63),
    ('nurture','nurture','to care for and help someone or something grow','Good coaches nurture confidence as well as skill.',64),
    ('perplex','perplex','to confuse someone because something is difficult to understand','The missing clue continued to perplex the detectives.',65),
    ('quaint','quaint','attractively old-fashioned or unusual','They stayed in a quaint cottage near the village square.',66),
    ('redundant','redundant','unnecessary because it is repeated or no longer needed','The editor removed a redundant sentence that repeated the point.',67),
    ('skeptical','skeptical','doubting that something is true or useful','The skeptical reviewer asked for evidence before agreeing.',68),
    ('transient','transient','lasting only for a short time','The transient glow faded a few seconds after the flash.',69),
    ('unanimous','unanimous','agreed by everyone involved','The panel reached a unanimous decision after discussion.',70),
    ('versatile','versatile','able to be used for many different purposes','A versatile tool can handle several kinds of repair.',71),
    ('whimsical','whimsical','playfully unusual or imaginative','The children loved the whimsical drawings in the book.',72),
    ('zeal','zeal','great energy and enthusiasm for an activity','He approached the science project with real zeal.',73),
    ('allocate','allocate','to give something to a particular person or purpose','The teacher will allocate ten minutes to each activity.',74),
    ('diminish','diminish','to become or make something smaller or less important','The sound began to diminish as the train moved away.',75),
    ('explicit','explicit','clear and fully expressed, leaving little doubt','The recipe gives explicit instructions for each step.',76),
    ('feasible','feasible','possible and practical to do successfully','The team chose a feasible plan that fit the available time.',77),
    ('genuine','genuine','real, true, or honestly felt','Her genuine apology showed that she understood the mistake.',78),
    ('hasty','hasty','done too quickly, often without enough thought','A hasty answer can miss an important detail in the question.',79),
    ('intricate','intricate','having many connected parts or fine details','The intricate map showed every path through the old city.',80)
),
word_rows as (
    select 'word-v2-' || w.seed_key as seed_key,
           w.term, w.definition, w.example, w.position,
           array_agg(c.option_text order by md5(w.seed_key || c.option_key)) as options
    from word_seed w
    join lateral (
        select candidates.option_key, candidates.option_text
        from (
            select w.seed_key as option_key, w.definition as option_text
            union all
            select other.seed_key, other.definition
            from word_seed other where other.seed_key <> w.seed_key
        ) candidates
        order by case when candidates.option_key = w.seed_key then 0 else 1 end,
                 md5(w.seed_key || candidates.option_key)
        limit 4
    ) c on true
    group by w.seed_key, w.term, w.definition, w.example, w.position
),
word_rows_final as (
    select seed_key, 'word'::text as category_id, 'word.meanings'::text as topic_slug,
           'In the sentence “' || example || ',” what does “' || term || '” mean?' as prompt,
           options, array_position(options, definition) - 1 as correct_index,
           term || ' means ' || definition || '. In context: ' || example as explanation,
           'https://dictionary.cambridge.org/dictionary/english/' || term as source_url,
           'B'::text as source_grade, 'medium'::text as difficulty,
           'vocabulary'::text as skill, array['launch-v2','vocabulary']::text[] as tags,
           position
    from word_rows
),
riddles (
    seed_key, topic_slug, prompt, options, correct_index, explanation, source_url, difficulty, skill, position
) as (values
    ('handshakes','riddle.logic','Seven guests each shake hands with every other guest exactly once. How many handshakes happen?','{14,21,28,42}',1,'Each handshake is a unique pair of guests, so the count is 7 × 6 ÷ 2, which equals 21.','https://www.mathsisfun.com/combinatorics/combinations-permutations.html','medium','reasoning',81),
    ('podium','riddle.logic','Six runners finish with no ties. How many different ordered first-second-third podiums are possible?','{20,60,120,216}',2,'There are six choices for first, five remaining choices for second, and four for third: 6 × 5 × 4 = 120.','https://www.mathsisfun.com/combinatorics/combinations-permutations.html','medium','reasoning',82),
    ('book-orders','riddle.logic','How many different orders can three distinct books be placed on a shelf?','{3,6,9,12}',1,'There are three choices for the first position, two for the next, and one for the last, giving 3 × 2 × 1 = 6.','https://www.mathsisfun.com/combinatorics/combinations-permutations.html','easy','reasoning',83),
    ('weekday-100','riddle.logic','If today is Monday, what day of the week will it be 100 days from now?','{Monday,Tuesday,Wednesday,Thursday}',2,'A week has seven days and 100 leaves a remainder of two when divided by seven, so the day advances from Monday to Wednesday.','https://www.mathsisfun.com/numbers/modulo.html','medium','reasoning',84),
    ('clock-630','riddle.logic','At exactly 6:30, what is the smaller angle between the hands of an analogue clock?','{0 degrees,15 degrees,30 degrees,45 degrees}',1,'At 6:30 the minute hand points to 180 degrees and the hour hand is at 195 degrees, leaving a 15-degree angle.','https://www.mathsisfun.com/activity/clocks-angles.html','medium','reasoning',85),
    ('clock-240','riddle.logic','At exactly 2:40, what is the smaller angle between an analogue clock’s hands?','{140 degrees,150 degrees,160 degrees,170 degrees}',2,'The minute hand is at 240 degrees and the hour hand at 80 degrees; their smaller separation is 160 degrees.','https://www.mathsisfun.com/activity/clocks-angles.html','hard','reasoning',86),
    ('fraction-80','riddle.logic','What is three quarters of 80?','{20,40,60,70}',2,'Three quarters of 80 is 80 divided by four, then multiplied by three: 20 × 3 = 60.','https://www.mathsisfun.com/fractions.html','easy','application',87),
    ('percent-350','riddle.logic','What is 10 percent of 350?','{3.5,35,70,315}',1,'Ten percent is one tenth; one tenth of 350 is 35, measured in the same units as the original amount.','https://www.mathsisfun.com/percentage.html','easy','application',88),
    ('prime-next','riddle.logic','Which is the next prime number after 29?','{30,31,33,35}',1,'31 is prime because its only positive divisors are 1 and itself; the other choices have additional factors.','https://www.mathsisfun.com/prime_numbers.html','easy','reasoning',89),
    ('rectangle-area','riddle.logic','A rectangle is 8 cm long and 5 cm wide. What is its area?','{13 cm²,26 cm²,40 cm²,80 cm²}',2,'The area of a rectangle is length multiplied by width, so 8 cm × 5 cm equals 40 square centimetres.','https://www.mathsisfun.com/geometry/area.html','easy','application',90),
    ('triangle-angles','riddle.logic','Two angles in a triangle are 50 degrees and 60 degrees. What is the third angle?','{60 degrees,70 degrees,80 degrees,90 degrees}',1,'The interior angles of a triangle add to 180 degrees; 180 − 50 − 60 leaves 70 degrees.','https://www.mathsisfun.com/triangle.html','easy','application',91),
    ('square-perimeter','riddle.logic','A square has sides 7 cm long. What is its perimeter?','{14 cm,21 cm,28 cm,49 cm}',2,'A square has four equal sides, so its perimeter is four times one side: 4 × 7 cm = 28 cm.','https://www.mathsisfun.com/geometry/square.html','easy','application',92),
    ('rectangle-diagonal','riddle.logic','A 3-by-4 rectangle has a diagonal from one corner to the opposite corner. How long is it?','{4,5,6,7}',1,'The diagonal is the hypotenuse of a 3-4-5 right triangle, so its length is 5 units.','https://www.mathsisfun.com/pythagoras.html','medium','application',93),
    ('ratio-juice','riddle.logic','A drink uses 2 cups of concentrate for every 5 cups of water. How many cups of water go with 6 cups of concentrate?','{10,12,15,18}',2,'Six cups is three times the concentrate amount, so the water amount is also tripled: 5 × 3 = 15 cups.','https://www.mathsisfun.com/numbers/ratio.html','medium','application',94),
    ('rate-pages','riddle.logic','A printer makes 30 pages in 5 minutes at a steady rate. How many pages can it make in 8 minutes?','{40,45,48,60}',2,'The rate is 30 ÷ 5 = 6 pages per minute; in eight minutes it produces 6 × 8 = 48 pages.','https://www.mathsisfun.com/measure/speed-velocity.html','medium','application',95),
    ('average-ages','riddle.logic','The ages of three teammates are 12, 15, and 18. What is their average age?','{14,15,16,45}',1,'Add the three ages to get 45, then divide by three teammates; the average age is 15.','https://www.mathsisfun.com/mean.html','easy','application',96),
    ('missing-sum','riddle.logic','Four numbers have an average of 10. Three are 6, 8, and 12. What is the fourth number?','{10,12,14,16}',2,'An average of 10 across four numbers means their total is 40; 40 − 6 − 8 − 12 leaves 14.','https://www.mathsisfun.com/mean.html','medium','reasoning',97),
    ('two-dice-max','riddle.logic','What is the greatest possible total when two standard six-sided dice are rolled?','{10,11,12,13}',2,'Each standard die has a maximum face value of six, so the greatest total is 6 + 6 = 12.','https://www.mathsisfun.com/data/probability.html','easy','recall',98),
    ('coin-flips','riddle.logic','A fair coin is flipped twice. How many equally likely ordered outcomes are possible?','{2,3,4,6}',2,'Each flip has two possible results; the ordered pairs are HH, HT, TH, and TT, giving four outcomes.','https://www.mathsisfun.com/data/probability.html','medium','reasoning',99),
    ('marble-guarantee','riddle.logic','A bag has red, blue, and green marbles. How many marbles guarantee that at least two have the same colour?','{2,3,4,6}',2,'In the worst case the first three marbles are one of each colour; the fourth must match one of them.','https://www.mathsisfun.com/puzzles/','medium','reasoning',100),
    ('digit-sum','riddle.logic','What is the sum of the digits in 4,728?','{18,19,20,21}',3,'Add the digits directly: 4 + 7 + 2 + 8 = 21, using the ordinary place-value digits in the number.','https://www.mathsisfun.com/numbers/','easy','application',101),
    ('reverse-number','riddle.logic','A two-digit number has digits that add to 9. Reversing its digits makes the number 27 larger. What is the original number?','{36,45,54,63}',0,'36 reversed is 63, which is 27 larger; the original digits also add to 9.','https://www.mathsisfun.com/algebra/','hard','reasoning',102),
    ('age-difference','riddle.logic','A parent is 36 and a child is 12. In how many years will the parent be twice the child’s age?','{6,8,10,12}',3,'In 12 years their ages will be 48 and 24; 48 is twice 24.','https://www.mathsisfun.com/algebra/','medium','reasoning',103),
    ('calendar-days','riddle.logic','How many days are there in two ordinary weeks and three extra days?','{14,15,17,21}',2,'Two weeks contain 14 days; adding three more days gives 17.','https://www.mathsisfun.com/measure/time.html','easy','application',104),
    ('cut-rope','riddle.logic','A 2-metre rope is cut into 8 equal pieces. How long is each piece?','{20 cm,25 cm,30 cm,40 cm}',1,'Two metres is 200 centimetres; dividing 200 cm into eight equal pieces gives 25 cm each.','https://www.mathsisfun.com/measure/metric-system.html','easy','application',105),
    ('change-making','riddle.logic','How many 25-cent coins make one dollar?','{2,3,4,5}',2,'One dollar is 100 cents, and 100 divided by 25 equals four coins.','https://www.mathsisfun.com/money/money-decimals.html','easy','application',106),
    ('water-tank','riddle.logic','A tank is one quarter full. Adding 18 litres makes it half full. What is the tank’s full capacity?','{36 L,54 L,72 L,90 L}',2,'The added 18 litres represents the difference between one quarter and one half, which is one quarter of capacity; capacity is 18 × 4 = 72 litres.','https://www.mathsisfun.com/fractions.html','medium','reasoning',107),
    ('odd-one-out','riddle.wordplay','Which number does not belong with the others: 9, 16, 25, 27?','{9,16,25,27}',3,'9, 16, and 25 are perfect squares; 27 is not a square of a whole number.','https://www.mathsisfun.com/square-root.html','easy','reasoning',108),
    ('word-letters','riddle.wordplay','How many letters are in the English word “alphabet”?','{6,7,8,9}',2,'The word alphabet is spelled a-l-p-h-a-b-e-t, which contains eight letters.','https://dictionary.cambridge.org/dictionary/english/alphabet','easy','reasoning',109),
    ('word-month','riddle.wordplay','Which month name has the fewest letters in English?','{May,June,July,March}',0,'May has three letters, while June and July have four and March has five.','https://www.timeanddate.com/calendar/months/','easy','reasoning',110),
    ('word-vowels','riddle.wordplay','How many vowels are in the word “education” when counting a, e, i, o, and u?','{4,5,6,7}',1,'Education contains e, u, a, i, and o: five vowels under the stated counting rule.','https://dictionary.cambridge.org/dictionary/english/vowel','easy','reasoning',111),
    ('word-opposite','riddle.wordplay','Which word is the opposite of “increase”?','{Expand,Raise,Decrease,Enlarge}',2,'Decrease means to become or make smaller, which is the opposite direction from increase.','https://dictionary.cambridge.org/dictionary/english/decrease','easy','vocabulary',112),
    ('race-lap','riddle.lateral','A cyclist is on lap 8 of a 12-lap race. How many laps remain after this lap is completed?','{3,4,5,8}',1,'After completing lap 8, laps 9 through 12 remain: four laps.','https://dictionary.cambridge.org/dictionary/english/lap','easy','reasoning',113),
    ('queue-position','riddle.lateral','You are fifth in a queue and move ahead of the person in fourth place. What is your new position?','{Third,Fourth,Fifth,Second}',1,'Passing the person immediately ahead moves you from fifth place into fourth place.','https://dictionary.cambridge.org/dictionary/english/fourth','easy','reasoning',114),
    ('calendar-months','riddle.lateral','How many months of the year contain at least 28 days?','{1,2,11,12}',3,'Every month has at least 28 days; February has 28 or 29, and all other months have more.','https://www.timeanddate.com/calendar/months/','easy','reasoning',115),
    ('candles','riddle.lateral','A room has a candle, a fireplace, and a lamp. You have one match. What do you light first?','{Candle,Fireplace,Lamp,The match}',3,'Before lighting any of the three objects, you must use the match to create the first flame.','https://dictionary.cambridge.org/dictionary/english/match','easy','reasoning',116),
    ('electric-train','riddle.lateral','An electric train travels east while the wind blows west. Which way does its exhaust smoke blow?','{East,West,Up,It has no exhaust smoke}',3,'An electric train has no exhaust from a combustion engine, so the question’s wind direction does not create a smoke trail.','https://www1.eere.energy.gov/vehiclesandfuels/pdfs/basics/jtb_electric_vehicle.pdf','medium','reasoning',117),
    ('single-person','riddle.wordplay','A boat is full of people, yet there is not a single person aboard. How can that be?','{It is a model boat,They are all married,It is underwater,The boat is empty}',1,'The word single can mean unmarried; the statement works if everyone aboard is married.','https://dictionary.cambridge.org/dictionary/english/single','medium','reasoning',118),
    ('two-fathers','riddle.lateral','Two fathers and two sons go fishing. They catch three fish and each person gets one. What is the fewest number of people?','{3,4,5,6}',0,'There are three people: a grandfather, his son, and his grandson; the middle person is both a father and a son.','https://dictionary.cambridge.org/dictionary/english/grandfather','medium','reasoning',119),
    ('siblings-basket','riddle.lateral','Three siblings each take one orange from a basket, but one orange remains in the basket. How many oranges were there at the start?','{3,4,5,6}',1,'There were four oranges: three were taken, and the fourth stayed inside the basket.','https://dictionary.cambridge.org/dictionary/english/each','easy','reasoning',120)
),
all_rows as (
    select seed_key, category_id, topic_slug, prompt, options, correct_index, explanation,
           source_url, source_grade, difficulty, skill, tags, position from gk_rows
    union all
    select seed_key, category_id, topic_slug, prompt, options, correct_index, explanation,
           source_url, source_grade, difficulty, skill, tags, position from word_rows_final
    union all
    select 'riddle-v2-' || seed_key, 'riddle', topic_slug, prompt,
           string_to_array(trim(both '{}' from options), ','), correct_index,
           explanation, source_url, 'B', difficulty, skill,
           array['launch-v2','original-riddle']::text[], position
    from riddles
)
insert into public.quiz_questions (
    id, version, category_id, topic_id, locale, prompt, options, correct_index,
    explanation, source_url, source_grade, source_checked_at, difficulty, skill, tags, state
)
select md5('launch-v2-review:' || r.seed_key)::uuid, 1, r.category_id, t.id, 'en-IN',
       r.prompt, r.options, r.correct_index, r.explanation, r.source_url,
       r.source_grade::public.source_grade, date '2026-09-30',
       r.difficulty::public.question_difficulty, r.skill::public.question_skill,
       r.tags, 'review'::public.content_state
from all_rows r
join public.quiz_topics t on t.slug = r.topic_slug and t.category_id = r.category_id
on conflict (id, version) do nothing;

with items (seed_key, position) as (values
    ('gk-v2-planet-count',1),('gk-v2-dwarf-count',2),('gk-v2-mercury-nearest',3),('gk-v2-mercury-smallest',4),
    ('gk-v2-inner-planets',5),('gk-v2-milky-way',6),('gk-v2-solar-formation',7),('gk-v2-sun-matter',8),
    ('gk-v2-venus-moons',9),('gk-v2-pluto-moons',10),('gk-v2-jupiter-largest',11),('gk-v2-jupiter-day',12),
    ('gk-v2-red-spot',13),('gk-v2-jupiter-atmosphere',14),('gk-v2-ganymede',15),('gk-v2-outer-planets',16),
    ('gk-v2-earth-known-life',17),('gk-v2-asteroid-belt',18),('gk-v2-mom-date',19),('gk-v2-mom-vehicle',20),
    ('gk-v2-mom-arrival',21),('gk-v2-mom-payloads',22),('gk-v2-aditya-purpose',23),('gk-v2-aditya-distance',24),
    ('gk-v2-aditya-launch',25),('gk-v2-ch1-water',26),('gk-v2-ch1-date',27),('gk-v2-ch3-lander',28),
    ('gk-v2-ch3-rover',29),('gk-v2-ch3-date',30),('gk-v2-ch3-purpose',31),('gk-v2-xposat-purpose',32),
    ('gk-v2-xposat-launch',33),('gk-v2-un-languages-count',34),('gk-v2-un-language-arabic',35),
    ('gk-v2-un-charter-sign',36),('gk-v2-un-charter-date',37),('gk-v2-water-ocean-store',38),
    ('gk-v2-water-fresh',39),('gk-v2-earth-oceans',40),
    ('word-v2-abundant',41),('word-v2-arduous',42),('word-v2-coherent',43),('word-v2-concise',44),
    ('word-v2-diligent',45),('word-v2-elated',46),('word-v2-frugal',47),('word-v2-impartial',48),
    ('word-v2-inevitable',49),('word-v2-innovate',50),('word-v2-novice',51),('word-v2-profound',52),
    ('word-v2-scarce',53),('word-v2-serene',54),('word-v2-reluctant',55),('word-v2-vigilant',56),
    ('word-v2-endorse',57),('word-v2-hinder',58),('word-v2-immense',59),('word-v2-jovial',60),
    ('word-v2-keen',61),('word-v2-lethargic',62),('word-v2-mundane',63),('word-v2-nurture',64),
    ('word-v2-perplex',65),('word-v2-quaint',66),('word-v2-redundant',67),('word-v2-skeptical',68),
    ('word-v2-transient',69),('word-v2-unanimous',70),('word-v2-versatile',71),('word-v2-whimsical',72),
    ('word-v2-zeal',73),('word-v2-allocate',74),('word-v2-diminish',75),('word-v2-explicit',76),
    ('word-v2-feasible',77),('word-v2-genuine',78),('word-v2-hasty',79),('word-v2-intricate',80),
    ('riddle-v2-handshakes',81),('riddle-v2-podium',82),('riddle-v2-book-orders',83),('riddle-v2-weekday-100',84),
    ('riddle-v2-clock-630',85),('riddle-v2-clock-240',86),('riddle-v2-fraction-80',87),('riddle-v2-percent-350',88),
    ('riddle-v2-prime-next',89),('riddle-v2-rectangle-area',90),('riddle-v2-triangle-angles',91),('riddle-v2-square-perimeter',92),
    ('riddle-v2-rectangle-diagonal',93),('riddle-v2-ratio-juice',94),('riddle-v2-rate-pages',95),('riddle-v2-average-ages',96),
    ('riddle-v2-missing-sum',97),('riddle-v2-two-dice-max',98),('riddle-v2-coin-flips',99),('riddle-v2-marble-guarantee',100),
    ('riddle-v2-digit-sum',101),('riddle-v2-reverse-number',102),('riddle-v2-age-difference',103),('riddle-v2-calendar-days',104),
    ('riddle-v2-cut-rope',105),('riddle-v2-change-making',106),('riddle-v2-water-tank',107),('riddle-v2-odd-one-out',108),
    ('riddle-v2-word-letters',109),('riddle-v2-word-month',110),('riddle-v2-word-vowels',111),('riddle-v2-word-opposite',112),
    ('riddle-v2-race-lap',113),('riddle-v2-queue-position',114),('riddle-v2-calendar-months',115),('riddle-v2-candles',116),
    ('riddle-v2-electric-train',117),('riddle-v2-single-person',118),('riddle-v2-two-fathers',119),('riddle-v2-siblings-basket',120)
)
insert into public.question_pack_items (pack_id, question_id, question_version, position)
select p.id, md5('launch-v2-review:' || i.seed_key)::uuid, 1, i.position
from public.question_packs p cross join items i
where p.slug = 'launch-v2-review'
on conflict (pack_id, question_id, question_version) do nothing;

commit;
