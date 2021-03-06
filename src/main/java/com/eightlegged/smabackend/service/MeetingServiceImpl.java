package com.eightlegged.smabackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eightlegged.smabackend.entity.Meeting;
import com.eightlegged.smabackend.entity.Status;
import com.eightlegged.smabackend.entity.User;
import com.eightlegged.smabackend.repository.MeetingRepository;
import com.eightlegged.smabackend.repository.UserRepository;

@Service
public class MeetingServiceImpl implements MeetingService {

	private static final Logger logger = LoggerFactory.getLogger(MeetingServiceImpl.class);

	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public String createMeeting(Meeting meeting) {

		meetingRepository.save(meeting);

		for (int i = 0; i < meeting.getUserList().size(); i++) {
			System.out.println(meeting.getUserList().get(i).getEmail());
			setUser(meeting.getUserList().get(i).getEmail(), meeting.getId());
		}

		meetingRepository.save(meeting);

		logger.info("Meeting Created! Meeting ID: " + meeting.getId());

		return "{\"result\": \"SUCCESS\", \"MEETING_ID\":\"" + meeting.getId() + "\"}";
	}

	@Override
	public String deleteMeetingById(Long id) {
		meetingRepository.findOne(id).getUserList().clear();
		if (userRepository.findAll() != null) {
			for (int i = 0; i < userRepository.findAll().size(); i++) {
				userRepository.findAll().get(i).getMeetingList().clear();
			}
		}

		logger.info("Meeting Deleted! Meeting ID: " + meetingRepository.findOne(id).getId());
		meetingRepository.delete(meetingRepository.findOne(id));
		;

		return "{\"result\": \"DELETE\", \"MEETING_ID\":\"" + id + "\"}";
	}

	@Override
	public String startMeeting(Long id) {
		// TODO Auto-generated method stub
		Meeting meeting = meetingRepository.findOne(id);
		meeting.setStatus(Status.START);
		meetingRepository.save(meeting);
		logger.info("Meeting Started! Meeting ID: " + meetingRepository.findOne(id).getId());

		return "{\"result\": \"START\", \"MEETING_STATUS\":\"" + meetingRepository.findOne(id).getStatus() + "\"}";
	}

	@Override
	public String completeMeeting(Long id) {
		// TODO Auto-generated method stub
		Meeting meeting = meetingRepository.findOne(id);
		if (meeting != null) {
			meeting.setStatus(Status.COMPLETE);
			meetingRepository.save(meeting);
			logger.info("Meeting Finished! Meeting ID: " + meetingRepository.findOne(id).getId());

			return "{\"result\": \"FINISHED\", \"MEETING_STATUS\":\"" + meetingRepository.findOne(id).getStatus()
					+ "\"}";
		}
		return "{\"result\": \"FAIL\", \"MEETING_STATUS\":\"" + "MEETING_DOES_NOT_EXIST"
				+ "\"}";
	}

	@Override
	public Meeting findOne(Long id) {
		Meeting meeting = meetingRepository.findOne(id);
		return meeting;
	}

	@Override
	public List<User> userList(User user) {
		// TODO Auto-generated method stub
		List<User> userList = new ArrayList<User>();
		userList.add(user);
		return null;
	}

	@Override
	public void setUser(String email, Long id) {
		// TODO Auto-generated method stub
		User user = userRepository.findByEmail(email);
		Meeting meeting = meetingRepository.findOne(id);

		if (user != null) {
			List<Meeting> existList = user.getMeetingList();

			boolean save = true;

			if (!existList.isEmpty()) {
				for (int j = 0; j < existList.size(); j++) {
					if (id == existList.get(j).getId()) {
						save = false;
					}
				}
			}

			if (save == true) {
				existList.add(meeting);
				user.setMeetingList(existList);
			}

			logger.info("User(ID: " + user.getId() + ") Participate in Meeting(ID: "
					+ meetingRepository.findOne(id).getId() + ")");
		}
	}

	@Override
	public List<Meeting> findByStatus(Status status) {
		// TODO Auto-generated method stub
		List<Meeting> meeting = meetingRepository.findByStatus(status);
		return meeting;
	}
}