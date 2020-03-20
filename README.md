# Route0 for User (IL and In class flows)

In a course which is catering to many competencies, across domains, users may not have pre requisite competencies' mastery to enable smooth learning for the specified course. Route to destination or Route0 is a collections of Units offered to the user as a pre requisite for course. Note that route0 is optional and user may choose to accept it or reject it.


### Scope

Here are salient points of implementation:

- There will be two APIs exposed, one which will fetch the route0 content and another which can trigger the calculation of route0 of specified content for specified user
- The fetch API will return the route0 info if, 
    - user is doing IL and session token belongs to same user
    - or if user is in class, then
        - session token should be of either be teacher/co-teacher of class and user for which route0 content is fetched should be student of same class
        - or the user for which route0 content is being fetched, should be the same user for which session token is being used
- If the route0 content for user does not exists, then Http status of 404 will be sent. In addition, a request is going to be queued to create the route0 content for specified user. The downstream services will take care of not doing same job multiple times
- Once the route0 calculation is done for a user, class and course combination, it will be persisted. Next time onwards when route0 APIs are called, they will either fetch persisted data (in case of fetch API) or the downstream queue processor will make sure to check if route0 calculation is not done so far before doing actual work


Since, there is a need to persist this queue of requests, we should be using DB to maintain the list and update the status there. This in turn will give rise to batch job kind of model, where in Http API will keep on updating the queue, and batch model will pick up and do rescoping of content
 
### Build and Run

To create the shadow (fat) jar:

    ./gradlew

To run the binary which would be fat jar:

    java -jar route0.jar src/main/resources/route0.json

### Fetch Route0 API
- For route0 to be enabled, class setting should have route0 as true or in case of IL course version should be not null
- If this API is called for class or course (IL) for which route0 is not enable, it will throw an error
- if route0 is enabled, then look up in route0 store to see if there is route0 content available
    - If available, serve the data
    - Else, send 404. But also queue the request for route0 to be calculated
- Note that currently there are no checks to verify if class and course specified (in class context) are having an association. TODO

### Do Route0 API
- This API will be called internally only
- It will return 200 response directly and will delegate processing to worker threads on message bus
- The API payload would contain source, class id and member ids array (optional, present only in case where source is class join)
- First check to see if class is having route0 enabled. If not, then processing is done
- Now generate the data for messages based on source
    - class join, one event per member specified in payload will be created.
    - course assign to class, class members will be looked up and one event per member of class will be created
    - route0 setting change, class members will be looked up and one event per member of class will be created
    - OOB, this won't be used by API per se, but by READ handler to post message along with member id in case of 404
        - This may also be used as API to trigger route0 calculation on adhoc basis
- Note that here we won't validate if class member may have (with current UI flows may not be possible) route0 content already. That will be done downstream

### Design

#### Problem
- Have a batch processing kind of infra
- Should be able to do it in parallel
- Queueing should be backed by persistence
- Queueing should be open so that others can also request for queueing their stuff
- Avoid concurrent processing of same record

#### Solution
- Have a batch processing kind of infra using parallelization would be achieved using worker threads working over message bus
- For persistence DB will be used
- Event bus will provide a mechanism for open ended trigger end point
- Do redundant checks and store state to avoid multiple processing of same record

#### Facets of Design
- Core processing
    - Should be triggered with receiving some key to identify as to what needs processing
- Controller processing
    - Event bus to receive two kinds of messages
        - Queue
        - Process
    - Both of these events will be fire and forget
    - For Queue messages, entry will be made into DB table
    - Now some mechanism needs to be in place to to read the DB table and generate Process messages
    - Need a timer thread in place
    - The queue in DB is going to have a status field with values - null, dispatched, processing
    - Record will be inserted in queue with status as null
    - When timer thread picks that up and sends to message bus, it will be marked as dispatched
    - When worker threads pick up the record to process, they first check to see if the record is present in table with status as dispatched and record is not present in route0 table, then the record will be marked as processing and will be processed
    - Once processing is done, this record will be deleted from queue
    - For the first run of timer thread, it should clean up all statuses in DB queue so that they are picked up for processing downstream
    - The number of records that are read from DB/queue and dumped on to message bus for processing, needs to be configurable

### Terminologies (Technical model)

#### Competency
A point in two dimension of progression and domain. Belongs to a subject and has a code.

#### Competency Path
The progression between two given competencies iff they belong to same subject and have same domain dimension. There is a directionality associated with path which implies that if one swaps source and destination competencies, the obtained path will be same with different directionality.

#### Competency Map
A collection of competencies from which we can deduce the dimension of domains and progression. Competency map is analogus to geometrical plane. Since there could be multiple competencies per domain, one can draw one unique skyline and one unique earthline. If there is single competency per domain in competency map, then skyline and earthline are same.

#### Competency Line
Linear representation of given competencies in plane, if both dimensions are changing simultaneously. Note that if the domain dimension is constant then one will get Competency Path (from representation perspective on competency map). This is represented as Set of unique domain and progression combination where domain does not repeat.

### Competency Route
Give two competency lines, it denotes the competency path for each domain present in destination which is needed to collapse two lines. If the source line denotes learner proficiency skyline, and destination denotes earthline for competencies covered by course then competency route is called route to destination.

## Technical drilldown: Package structure and functions

Following is the list of packages and its contents. Note that abbreviated package names are used.

### o.g.r.bootstrap 
Contains the main runner class which has the main method.

### o.g.r.bootstrap.verticles
Housing for the verticles. There are four verticles as of now

### o.g.r.bootstrap.verticles.AuthVerticle
Authenticates session token with Redis

### o.g.r.bootstrap.verticles.HttpVerticle
Responsible for starting up HTTP server and registering routes

### o.g.r.bootstrap.verticles.Route0Verticle
The verticle which is main listener for API requests which is forwarded from Http server post authentication. 

### o.g.r.bootstrap.verticles.Route0ProcessingVerticle
This verticles receives a message for the queue record of rescope for processing. It takes that record and does the processing. Other components (even outside the process scope) can queue the records to get it processed.

### o.g.r.infra.components
This contains various components like config handler, data source registry etc. This also has mechanism to initialize components at the startup. Components are generally singleton.

### o.g.r.infra.components.Route0QueueReaderAndDispatcher
This is the timer based runner class which is responsible to read the Persisted queued requests and send them to Event bus so that they can be processed by listeners. It does wait for reply, so that we do not increase the backpressure on TCP bus too much, however what is replied is does not matter as we do schedule another one shot timer to do the similar stuff. For the first run, it re-initializes the status in the DB so that any tasks that were under processing when the application shut down happened would be picked up again.

### o.g.r.infra.constants
Housing for different constants used across the application.

### o.g.r.infra.data
This contains general POJO which are reusable across different modules in this application. 

### o.g.r.infra.data.competency
This package houses the whole algebra aspects of competency. This includes, but not limited to:
- Competency model
- Domain model
- Competency line 
- Competency Path
- Competency Route
- Progression Level (sequence id of competency)
- Subject model

This is base package responsible for doing algebra and unless there is a need to change the way algebra functions, this should be pretty constant.


### o.g.r.infra.exceptions
This contains exception classes which are reusable across different modules in this application. 

### o.g.r.infra.jdbi
This is JDBI specific package which contains helper entities like reusable mappers, argument factories, creators etc. This does not contain module specific DAO though. They are hosted with individual modules.

### o.g.r.infra.services
This houses infra structure services. The current services exposed are:
- ContentFetcherService: get collection metadata like title etc given collection ids
- Route0RequestQueueService: queue the request for doing route0 for given context
- Route0QueueInitializerService: initialize the machinery when the application starts
- Route0ProcessingService: real work horse service which acts as entry point to do route0 calculation

### o.g.r.infra.services.competencyroutecalculator
The business logic to do competency route calculation using the algebra.

### o.g.r.infra.services.competencyroutetocontentroutemapper
The service to convert a given competency route to a content route

### o.g.r.infra.services.contentroutepersister
The service which can persist the content route

### o.g.r.infra.services.fetchclass
The service to fetch class entity

### o.g.r.infra.services.r0applicable
The service to ascertain if the route0 is applicable in given context

### o.g.r.infra.services.suggestionprovider
Fetch the suggestions from competency_content_map, personalized for specified user for all specified competencies. With current implementation, no personalization is applied though weight is used for ordering. The list should contain one collection and one assessment for each competency at most, in that order.

### o.g.r.infra.utils
Different utility classes

### o.g.r.processors
The processors which are used as handlers for APIs

### o.g.r.processors.acceptrejectroute0
The processing handlers for API backend to catch request for doing route0. This results in queue of request.

### o.g.r.processors.calculatecompetencycontentroute
API handler to create competency and content route

### o.g.r.processors.calculatecompetencyroute
API handler to calculate the competency route only. There is no content route created/persisted.

### o.g.r.processors.doroute0ofcontent
API handlers to process API meant for triggering route0 request for a given context

### o.g.r.processors.fetchroute0ofcontent
API handlers to process API for fetching route0 content for a given context

### o.g.r.processors.fetchrescopedcontent
The processing handlers for API backend to fetch rescoped content for a specified user/class context.

### o.g.s.responses.*
Package to handler http response writing and passing it on

### o.g.s.routes.*
Utilities for the route registration for http handling and payload creation to be passes on to downstream processors.


