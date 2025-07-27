from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime



app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+mysqlconnector://root:tiger@localhost/eventdb'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

class Event(db.Model):
    __tablename__ = 'event'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(80), nullable=False)
    description = db.Column(db.String(300), nullable = False)
    max_seats = db.Column(db.Integer)
    seats_filled = db.Column(db.Integer, default = 0)
    deadline = db.Column(db.DateTime)
    rsvps = db.relationship('RSVP')

class RSVP(db.Model):
    __tablename__ = 'RSVP'

    id = db.Column(db.Integer, primary_key=True)
    event_id = db.Column(db.Integer , db.ForeignKey('event.id'))
    email = db.Column(db.String(100))
    status = db.Column(db.String(50))
    time = db.Column(db.DateTime, default = datetime.utcnow())


@app.route('/events', methods=['POST'])
def create_event():
    data = request.json
    event = Event(
        name=data['name'],
        description=data['description'],
        max_seats=data['max_seats'],
        deadline=data['deadline']
    )
    db.session.add(event)
    db.session.commit()
    return jsonify({"message": f"Event created", "id": event.id})

@app.route('/events', methods=['GET'])
def all_events():
    events = Event.query.filter_by().all()
    eventsList = [{ 'name': r.name, 'description': r.description, 'max_seats': r.max_seats, 'seats_filled': r.seats_filled, 'deadline': r.deadline } for r in events ]
    return jsonify({
        'events' : eventsList
    })

@app.route('/events/<int:event_id>', methods=['GET'])
def show_event(event_id):
    event = Event.query.get(event_id)
    rsvps = RSVP.query.filter_by(event_id = event_id).all()
    registered = [{ 'email': r.email, 'status': r.status, 'time': r.time } for r in rsvps ]
    return jsonify({
        "name": event.name,
        "description": event.description,
        "max_seats": event.max_seats,
        "seats_filled": event.seats_filled,
        "deadline": event.deadline,
        "registrations": registered
    })

@app.route('/rsvp/<int:event_id>', methods=['POST'])
def rsvp(event_id):
    event = Event.query.get(event_id)
    if datetime.utcnow() > event.deadline:
        return jsonify({"error": "Registration deadline is over"})
    data = request.json
    email = data.get('email')

    existing = RSVP.query.filter_by(event_id = event_id, email = email).first()
    if existing:
        return jsonify({"message": "Already registered", "status": existing.status})

    if event.seats_filled < event.max_seats:
        status = "confirmed"
        event.seats_filled+=1
    else:
        status = "waitlisted"

    rsvp = RSVP(event_id = event_id, email = email, status = status)

    db.session.add(rsvp)
    db.session.commit()

    return jsonify({"Message": f"RSVP {status}"})

@app.route('/cancel/<int:event_id>', methods=['POST'])
def cancel_rsvp(event_id):
    data = request.json
    email = data.get('email')
    rsvp = RSVP.query.filter_by(event_id = event_id, email = email).first()
    if not rsvp:
        return jsonify({"error":"RSVP not found"})

    db.session.delete(rsvp)

    if rsvp.status == "confirmed":
        event = Event.query.get(event_id)
        event.seats_filled -= 1

        waitlisted = RSVP.query.filter_by(event_id=event_id, status = "waitlisted").order_by(RSVP.time).first()

        if waitlisted:
            waitlisted.status = "confirmed"
            event.seats_filled += 1
            print(f"Mock email: {waitlisted.email} moved from waitlist to confirmed")

    db.session.commit()
    return jsonify({"message": "RSVP cancelled"})



if __name__ == "__main__":
    with app.app_context():
        db.create_all()
    app.run(debug=True)
