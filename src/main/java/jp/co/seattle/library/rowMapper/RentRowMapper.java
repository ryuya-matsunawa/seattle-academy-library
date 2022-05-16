package jp.co.seattle.library.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import jp.co.seattle.library.dto.RentBookDetailsInfo;

@Configuration
public class RentRowMapper implements RowMapper<RentBookDetailsInfo> {

	@Override
	public RentBookDetailsInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		// Query結果（ResultSet rs）を、オブジェクトに格納する実装
		RentBookDetailsInfo rentInfo = new RentBookDetailsInfo();
		rentInfo.setRentBookId(rs.getInt("rentbook_id"));
		rentInfo.setBookId(rs.getInt("id"));
		
		return rentInfo;
	}

}